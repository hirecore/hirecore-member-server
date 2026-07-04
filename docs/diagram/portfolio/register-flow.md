# 포트폴리오 등록 플로우

`Presigned URL 발급 → S3 직접 업로드 → 포트폴리오 등록` 한 사이클입니다.
`file` 모듈과 `portfolio` 모듈 두 BC에 걸쳐 있습니다.

## 계약 플로우 (Client ↔ Backend)

> 클라이언트 관점의 호출 순서. 내부 어댑터는 감춘 인터랙션 고도.

```mermaid
sequenceDiagram
    actor Client as Client (FE)
    participant API as Member Server
    participant S3

    Note over Client,S3: 1) Presigned URL 발급
    Client->>API: POST /api/users/files/images/presigned-put-url
    API-->>Client: presignedUrl, publicUrl, imageFileMetaId (상태=PENDING)

    Note over Client,S3: 2) S3 직접 업로드 (서버 미경유)
    Client->>S3: PUT presignedUrl (binary)
    S3-->>Client: 200 OK

    Note over Client,S3: 3) 포트폴리오 등록
    Client->>API: POST /api/portfolios (imageFileMetaId 들 포함)
    API-->>Client: 201 Created (portfolioId) · 이미지 PENDING→UPLOADED 확정
```

## 내부 처리 흐름

> 백엔드 개발자가 참조하는 실제 계층 흐름과 실패 분기.

### 1) Presigned URL 발급

```mermaid
sequenceDiagram
    actor Client
    participant Ctrl as UserFileCommandController
    participant UC as IssueImageUploadUrlUseCase
    participant S3Adapter as S3PresignedPutUrlAdapter
    participant Meta as ImageFileMeta
    participant Repo as ImageFileMetaJpaCommandAdapter
    participant S3

    Client->>Ctrl: POST /files/images/presigned-put-url
    Ctrl->>UC: execute(memberId, commandList)
    UC->>UC: verifyUserStorageCapacityPort.verifyCapacityFor()
    alt 용량 초과 (업로드요청 + 사용중 > 한도)
        UC-->>Ctrl: STORAGE_QUOTA_EXCEEDED 예외
        Ctrl-->>Client: 400 Bad Request (저장 공간 부족)
    else 용량 충분
        loop 요청된 파일마다
            UC->>S3Adapter: generate(objectKey, contentType, size)
            S3Adapter->>S3: presignPutObject()
            S3-->>S3Adapter: presignedUrl
            S3Adapter-->>UC: PresignedPutUrl(presignedUrl, publicUrl)
            UC->>Meta: create() · status=PENDING
            UC->>Repo: save(imageFileMeta)
            Repo-->>UC: imageFileMetaId
        end
        UC-->>Ctrl: responses
        Ctrl-->>Client: 200 OK (presignedUrl, publicUrl, imageFileMetaId)
    end
```

### 2) S3 직접 업로드

서버를 거치지 않습니다. 이 시점에도 서버가 아는 상태는 여전히 `PENDING` 입니다.

```mermaid
sequenceDiagram
    actor Client
    participant S3
    Client->>S3: PUT presignedUrl (binary)
    S3-->>Client: 200 OK
```

### 3) 포트폴리오 등록

참조된 이미지를 `PENDING → UPLOADED` 로 확정한 뒤 포트폴리오를 저장합니다.

```mermaid
sequenceDiagram
    actor Client
    participant Ctrl as PortfolioCommandController
    participant UC as CreatePortfolioUseCase
    participant Img as ImageFileMeta
    participant Domain as Portfolio
    participant Repo as PortfolioJpaCommandAdapter

    Client->>Ctrl: POST /api/portfolios
    Ctrl->>UC: execute(memberId, command)

    UC->>Img: markUploaded() 시도 (존재·소유·상태 검증)
    alt 이미지 검증 실패
        Img-->>Ctrl: 검증 예외
        Note over Img,Ctrl: 존재하지 않음 404 · 소유자 불일치 403 · 잘못된 상태 전이 409
        Ctrl-->>Client: 4xx (검증 실패)
    else 검증 통과
        Img-->>UC: PENDING→UPLOADED 확정
        Note right of Img: ImageUploadedEvent 등록
        UC->>UC: loadJobCategoryPort.findIdByCode(code)
        alt 카테고리 코드 없음/비활성
            UC-->>Ctrl: JOB_CATEGORY_CODE_NOT_FOUND
            Ctrl-->>Client: 404 Not Found (유효하지 않은 카테고리 코드)
        else 코드 유효
            UC->>Domain: create() · status=PUBLISHED
            UC->>Repo: save(portfolio)
            Repo-->>UC: portfolioId
            UC-->>Ctrl: portfolioId
            Ctrl-->>Client: 201 Created (Location 헤더)
        end
    end
    Note over UC: 성공 시 커밋 후 ImageUploadedEvent 발행 → 저장 용량 사용량 반영
```

> 위 분기 외에, 본문(content) JSON 직렬화가 실패하면 `CONTENT_JSON_SERIALIZATION_FAILED` → **500 Internal Server Error**(기술적 오류)로 응답합니다.

---

## 구조 · 헥사고날 계층

> 위 흐름을 계층 관점에서 본 것. 의존 방향은 `adapter.in → application(port) → domain`, 그리고 `application(port) ← adapter.out`.
> 애플리케이션은 **포트(interface)에만** 의존하고, 어댑터가 이를 구현한다. (전체 아키텍처는 [../ARCHITECTURE.md](../ARCHITECTURE.md) 참고)

```mermaid
flowchart LR
    Client([Client / FE])

    subgraph web[adapter.in.web]
        Ctrl[PortfolioCommandController]
    end

    subgraph app[application]
        UC[CreatePortfolioUseCase]
        SavePort{{SavePortfolioPort}}
        MarkPort{{MarkImagesAsUploadedPort}}
        JobPort{{LoadJobCategoryPort}}
    end

    subgraph domain[domain]
        Portfolio[Portfolio]
    end

    subgraph out[adapter.out]
        PfAdapter[PortfolioJpaCommandAdapter]
        DB[(portfolio RDB)]
    end

    subgraph file[file 모듈 · adapter.in.shared]
        FileAdapter[ImageFileMetaSharedCommandAdapter]
    end

    Client -->|POST /api/portfolios| Ctrl
    Ctrl -->|execute| UC
    UC --> Portfolio
    UC -.-> SavePort
    UC -.-> MarkPort
    UC -.-> JobPort
    SavePort -.->|구현| PfAdapter
    MarkPort -.->|구현| FileAdapter
    PfAdapter --> DB
```

- `{{...}}`(육각형) = 포트(interface)
- BC 경계를 넘는 호출(`MarkImagesAsUploadedPort`)은 **포트로만** 이루어지며, `file` 모듈의 shared 어댑터가 구현한다.

# 포트폴리오 등록 — 내부 시퀀스 (온보딩용)

> 독자: 신규 입사자 / 내부 구현을 이해해야 하는 개발자
> FE용 계약 플로우(인터랙션 고도)는 [../README.md](../README.md#5-계약-플로우-fe--backend) 참고.

`Presigned URL 발급 → S3 직접 업로드 → 포트폴리오 등록` 한 사이클을 내부 계층까지 보여줍니다.
`file` 모듈과 `portfolio` 모듈 두 BC에 걸쳐 있습니다.

## 1) Presigned URL 발급

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

## 2) S3 직접 업로드

서버를 거치지 않습니다. 이 시점에도 서버가 아는 상태는 여전히 `PENDING` 입니다.

```mermaid
sequenceDiagram
    actor Client
    participant S3
    Client->>S3: PUT presignedUrl (binary)
    S3-->>Client: 200 OK
```

## 3) 포트폴리오 등록

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
    UC->>Img: markUploaded() · PENDING→UPLOADED
    Note right of Img: ImageUploadedEvent 등록
    UC->>UC: loadJobCategoryPort.findIdByCode(code)
    UC->>Domain: create() · status=PUBLISHED
    UC->>Repo: save(portfolio)
    Repo-->>UC: portfolioId
    UC-->>Ctrl: portfolioId
    Ctrl-->>Client: 201 Created (Location 헤더)
    Note over UC: 커밋 후 ImageUploadedEvent 발행 → 저장 용량 사용량 반영
```

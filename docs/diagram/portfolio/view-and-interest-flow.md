# 포트폴리오 조회·관심·조회수 흐름

포트폴리오 **등록 이후의 상호작용** — 상세 조회(조회수 집계), 관심 등록/해제 — 을 다룬다.
등록 사이클은 [register-flow.md](register-flow.md) 참고.

> 핵심: 조회수·관심수는 **캐시 값**(`cachedViewCount`·`cachedInterestCount`)으로 들고, 실제 기록(`PortfolioMemberView`·`PortfolioMemberInterest`)은 **도메인 이벤트**로 반영한다. 조회수와 관심의 이벤트 처리 시점이 다르다.

---

## 상세 조회 · 조회수

> 읽기 유스케이스가 조회수 이벤트를 등록하고, **커밋 후 별도 트랜잭션**에서 기록한다.

```mermaid
sequenceDiagram
    actor Client
    participant Ctrl as PortfolioQueryController
    participant UC as LoadPortfolioDetailUseCase
    participant PF as Portfolio
    participant L as PortfolioViewedListener

    Client->>Ctrl: GET /api/portfolios/{id} (비로그인 허용)
    Ctrl->>UC: execute(portfolioId, viewerId)
    UC->>PF: LoadPortfolioPort.findById()
    alt 없음
        UC-->>Client: 404 PORTFOLIO_NOT_FOUND
    else PRIVATE & 비소유자
        UC-->>Client: 403 PORTFOLIO_FORBIDDEN
    else 조회 가능
        UC->>UC: LoadProfileNicknameSharedPort.findNickname()
        UC->>UC: ExistsPortfolioMemberViewPort.exists() · 중복 조회 확인
        UC->>PF: markViewedBy(viewerId, alreadyViewed)
        Note right of PF: 로그인 & 비소유자 & 미조회 → PortfolioViewedEvent 등록
        UC->>UC: PublishDomainEventsSharedPort.publishAll()
        UC-->>Client: 200 OK · 조회수 = 캐시 + (기록되면 1)
        Note over UC,L: 트랜잭션 커밋 후 (AFTER_COMMIT · REQUIRES_NEW)
        PF-->>L: PortfolioViewedEvent
        L->>L: PortfolioMemberView 저장 + 조회수 +1
        Note right of L: UNIQUE(portfolio, member) 위반 시 무시 (멱등)
    end
```

---

## 관심 등록 / 해제

> 쓰기 유스케이스라 리스너가 **같은 트랜잭션에서 동기**로 카운트를 갱신한다.

```mermaid
sequenceDiagram
    actor Client
    participant Ctrl as PortfolioCommandController
    participant UC as Register / CancelPortfolioInterestUseCase
    participant PF as Portfolio
    participant L as PortfolioInterestListener

    rect rgba(33, 150, 243, 0.12)
    Note over Client,L: 관심 등록
    Client->>Ctrl: POST /api/portfolios/{id}/interest
    Ctrl->>UC: execute(portfolioId, memberId)
    UC->>PF: LoadPortfolioPort.findById()
    UC->>UC: ExistsPortfolioMemberInterestPort.exists()
    UC->>PF: registerInterestBy(memberId, alreadyInterested)
    Note right of PF: 소유자 거부 · 비공개 거부 · 이미 관심이면 멱등(이벤트 미발행)
    UC->>UC: publishAll() → PortfolioInterestRegisteredEvent
    PF-->>L: (동기) 관심 저장 + 관심수 +1
    Ctrl-->>Client: 성공
    end

    rect rgba(244, 67, 54, 0.10)
    Note over Client,L: 관심 해제
    Client->>Ctrl: DELETE /api/portfolios/{id}/interest
    Ctrl->>UC: execute(portfolioId, memberId)
    UC->>PF: cancelInterestBy(memberId) → PortfolioInterestCancelledEvent
    PF-->>L: (동기) 관심 삭제 · 삭제됐으면 관심수 -1
    Ctrl-->>Client: 성공
    end
```

---

## 실패 분기

| 상황 | 응답 |
|---|---|
| 포트폴리오 없음 | 404 (`PORTFOLIO_NOT_FOUND` / `INTEREST_PORTFOLIO_NOT_FOUND`) |
| 비공개(PRIVATE) 조회·관심 (비소유자) | 403 (`…_FORBIDDEN`) |
| 소유자가 자기 글에 관심 | `INTEREST_OWNER_NOT_ALLOWED` |
| 이미 관심 / 미관심 반복 | 멱등 (오류 없음) |

> **이벤트 처리 시점 대비**
> - **조회수**: 읽기(`readOnly`) 유스케이스 → `AFTER_COMMIT` + `REQUIRES_NEW` (별도 쓰기 트랜잭션). 본인 조회는 도메인에서 제외.
> - **관심**: 쓰기 유스케이스 → `@EventListener` 동기(현재 트랜잭션). 등록은 `alreadyInterested`, 해제는 삭제행 수로 멱등 제어.

---

## 구조 · 헥사고날 계층

> 조회·관심 유스케이스의 계층 흐름. 의존은 `adapter.in → application(port) → domain`, `application(port) ← adapter.out`.
> 애그리거트·자식·불변식 등 **도메인 모델은 [domain-model.md](domain-model.md)** 로 분리했다.

```mermaid
flowchart LR
    Client([Client / FE])

    subgraph web[adapter.in.web]
        QCtrl[PortfolioQueryController]
        CCtrl[PortfolioCommandController]
    end
    subgraph app[application]
        DUC[LoadPortfolioDetailUseCase]
        IUC[Register·CancelPortfolioInterestUseCase]
        LoadP{{LoadPortfolioPort}}
        ExistsP{{Exists...MemberView·InterestPort}}
        Nick{{LoadProfileNicknameSharedPort}}
        Pub{{PublishDomainEventsSharedPort}}
    end
    subgraph domain[domain]
        PF[Portfolio]
    end
    subgraph out[adapter.out]
        PQ[PortfolioJpaQueryAdapter]
        PC[PortfolioJpaCommandAdapter]
        DB[(portfolio RDB)]
    end
    subgraph ev[adapter.in.event]
        VL[PortfolioViewedListener]
        IL[PortfolioInterestListener]
    end

    Client -->|GET 상세| QCtrl
    Client -->|POST·DELETE 관심| CCtrl
    QCtrl -->|execute| DUC
    CCtrl -->|execute| IUC
    DUC --> PF
    IUC --> PF
    DUC -.-> LoadP
    DUC -.-> Nick
    DUC -.-> Pub
    IUC -.-> LoadP
    IUC -.-> ExistsP
    IUC -.-> Pub
    LoadP -.->|구현| PQ
    PQ --> DB
    PF -. 이벤트 .-> VL
    PF -. 이벤트 .-> IL
    VL --> PC
    IL --> PC
    PC --> DB
```

- `{{...}}`(육각형) = 포트(interface). 조회수·관심수 갱신은 **이벤트 리스너**가 `adapter.out` 커맨드 어댑터로 수행한다.
- 도메인 애그리거트·자식 엔티티·불변식은 [domain-model.md](domain-model.md) 로 분리했다.

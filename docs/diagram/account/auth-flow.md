# 소셜 로그인·인증 흐름

`account` 모듈의 **OAuth2 소셜 로그인**과, 이후 모든 요청에 걸리는 **JWT 인증**을 다룬다.
토큰(accessToken·refreshToken)은 **쿠키**로 오간다. 계층 일반론은 [../ARCHITECTURE.md](../ARCHITECTURE.md) 참고.

## 계약 플로우 (Client ↔ Backend)

> 클라이언트 관점의 호출 순서. 내부 어댑터는 감춘 인터랙션 고도.

```mermaid
sequenceDiagram
    actor Client as Client (FE)
    participant API as Member Server
    participant Social as 소셜 (Kakao)

    Note over Client,Social: 1) 소셜 로그인
    Client->>API: POST /api/auth/login/user/{provider} (authorizationCode)
    API->>Social: 인가코드로 토큰·프로필 조회
    Social-->>API: 사용자 프로필(providerId·email·닉네임·동의)
    alt 신규회원
        API->>API: 프로필·소셜계정 자동 생성
    end
    API-->>Client: 200 OK · Set-Cookie(accessToken, refreshToken)
    

    Note over Client,Social: 2) 인증된 요청
    Client->>API: 임의 API (accessToken 쿠키 동봉)
    API-->>Client: 토큰·버전 검증 후 처리

    Note over Client,Social: 3) 로그아웃
    Client->>API: POST /api/auth/logout
    API-->>Client: 쿠키 만료 · 기존 토큰 전부 무효화
```

---

## 내부 처리 · 소셜 로그인

> 신규/기존 회원 분기와 도메인 이벤트로 인한 부수 생성(프로필·소셜계정)을 담은 백엔드 고도.

```mermaid
sequenceDiagram
    actor Client
    participant Ctrl as AuthCommandController
    participant UC as LoginSocialUserUseCase
    participant Fetch as FetchSocialUserProfilePort
    participant Kakao as Kakao OAuth2
    participant Member as MemberAccount
    participant Token as IssueTokenPort
    participant PL as MemberSignupListener (profile)
    participant AL as SocialSignupListener (account)

    Client->>Ctrl: POST /api/auth/login/user/{provider}<br/>authorizationCode
    Ctrl->>UC: execute(SocialLoginCommand)
    UC->>Fetch: fetchByAuthorizationCode(code)
    Fetch->>Kakao: 토큰 발급 + 프로필 조회
    Kakao-->>Fetch: providerId · email · nickname · 동의여부
    Fetch-->>UC: SocialUserProfileResult

    alt 기존 소셜계정 존재
        UC->>UC: LoadSocialAccountPort · LoadMemberAccountPort
    else 신규 회원
        UC->>Member: createWithSocial()
        Note right of Member: 도메인 이벤트 2건 등록<br/>(MemberAccountCreated · MemberSocialSignedUp)
        UC->>UC: SaveMemberAccountPort.save()
        Note over UC,AL: 저장 시 이벤트 발행 → 리스너 동기 실행 (같은 트랜잭션)
        UC->>PL: MemberAccountCreatedEvent
        PL->>PL: 프로필 생성·저장
        UC->>AL: MemberSocialSignedUpEvent
        AL->>AL: SocialAccount 생성·저장
    end

    UC->>Token: issueTokenPair(TokenClaimsRequest)
    Token-->>UC: accessToken · refreshToken
    UC-->>Ctrl: PairTokenResponse
    Ctrl-->>Client: 200 OK · Set-Cookie(accessToken, refreshToken)
```

> 로그인 처리는 `TransactionTemplate` 로 감싼 한 트랜잭션이다. `save` 시 `@DomainEvents` 로 이벤트가 발행되고, `@EventListener` 리스너가 **같은 트랜잭션 안에서 동기로** 프로필(`profile`)·소셜계정(`account`)을 만든다.

---

## 요청 인증 (매 요청 · 횡단)

> `account` 가 제공하는 공용 포트를 `common` 의 필터가 모든 요청에서 사용한다.

```mermaid
sequenceDiagram
    actor Client
    participant Filter as JwtAuthenticationFilter
    participant Resolve as ResolveTokenSharedPort
    participant Verify as ValidateTokenVersionSharedPort
    participant Ctrl as 대상 컨트롤러

    Client->>Filter: 요청 (accessToken 쿠키)
    alt 토큰 없음 · 만료 · 위조 · 버전 불일치
        Filter-->>Client: 401 Unauthorized (ApiAuthenticationEntryPoint)
    else 유효
        Filter->>Resolve: resolveToken(token)
        Resolve-->>Filter: AuthPrincipal(id, email, role, tokenVersion)
        Filter->>Verify: isValidTokenVersion(id, tokenVersion)
        Verify-->>Filter: true
        Filter->>Ctrl: SecurityContext 설정 후 통과
    end
```

## 로그아웃 · 토큰 무효화

```mermaid
sequenceDiagram
    actor Client
    participant Ctrl as AuthCommandController
    participant UC as LogoutUseCase
    participant Repo as IncrementTokenVersionPort

    Client->>Ctrl: POST /api/auth/logout
    Ctrl->>UC: execute(memberId)
    UC->>Repo: incrementTokenVersion(memberId)
    Note over Repo: tokenVersion++ → 발급됐던 모든 토큰이 버전검증에서 탈락
    UC-->>Ctrl: 완료
    Ctrl-->>Client: 200 OK · 쿠키 만료
```

---

## 실패 분기

| 상황 | 응답 |
|---|---|
| 카카오 API 실패 | 카카오 응답 상태 전파(4xx/5xx) |
| 이메일/닉네임 동의 누락 | 400 (`SocialAccountDomainException`) |
| 토큰 없음·만료·위조·버전 불일치 | 401 (`ApiAuthenticationEntryPoint`) |
| 기존 회원 조회 정합성 오류 | 500 (`DataConsistencyException`) |

## 도메인 이벤트

| 이벤트 | 발행 | 구독(리스너) | 결과 |
|---|---|---|---|
| `MemberAccountCreatedEvent` | `MemberAccount.createWithSocial()` | profile · `MemberSignupListener` (동기) | 프로필 자동 생성 |
| `MemberSocialSignedUpEvent` | 〃 | account · `SocialSignupListener` (동기) | `SocialAccount` 저장 |

---

## 구조 · 헥사고날 계층

```mermaid
flowchart LR
    Client([Client / FE])

    subgraph web[adapter.in.web]
        Ctrl[AuthCommandController]
    end
    subgraph app[application]
        UC[LoginSocialUserUseCase]
        Fetch{{FetchSocialUserProfilePort}}
        Save{{SaveMemberAccountPort}}
        Issue{{IssueTokenPort}}
    end
    subgraph domain[domain]
        Member[MemberAccount · SocialAccount]
    end
    subgraph out[adapter.out]
        Kakao[FetchKakaoUserProfileAdapter]
        Jwt[TokenProviderAdapter]
        DB[(account RDB)]
    end
    subgraph sec[common.security · 횡단]
        JwtF[JwtAuthenticationFilter]
    end

    Client -->|POST /api/auth/login| Ctrl
    Ctrl -->|execute| UC
    UC --> Member
    UC -.-> Fetch
    UC -.-> Save
    UC -.-> Issue
    Fetch -.->|구현| Kakao
    Issue -.->|구현| Jwt
    Save -.->|구현| DB
    Client -.->|그 외 모든 요청| JwtF
    JwtF -.->|ResolveToken · ValidateTokenVersion SharedPort| UC
```

- `{{...}}`(육각형) = 포트(interface). 어댑터가 구현한다.
- 인증 필터는 특정 API가 아니라 **모든 요청**에 걸리는 횡단 관심사다.

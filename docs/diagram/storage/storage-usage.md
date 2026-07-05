# 스토리지 용량 흐름 (할당량·사용량)

**할당량**은 `membership`, **사용량**은 `storage` 가 관리한다. 두 값을 합쳐 업로드 가능 여부를 판정하고, 실제 사용량은 `file` 의 이미지 이벤트로 증감한다.
이미지 자체의 상태 생애주기는 [../file/image-lifecycle.md](../file/image-lifecycle.md) 참고.

## 개념

| 개념 | 모듈 | 저장 | 의미 |
|---|---|---|---|
| **할당량(quota)** | membership | `UserMembershipEntitlement.storageQuotaBytesSnapshot` | 등급별 한도. **부여 시점 스냅샷**(정책이 바뀌어도 고정) |
| **사용량(usage)** | storage | `UserStorageUsage.usedQuotaBytes` (+ `UserStorageUsageLog`) | 현재 사용 바이트와 변경 이력 |

---

## 흐름 1 · 발급 전 용량 검증

`file` 이 presigned URL을 발급하기 전에, 요청 크기를 더해도 한도를 넘지 않는지 확인한다.

```mermaid
sequenceDiagram
    participant File as file · IssueImageUploadUrlUseCase
    participant Verify as VerifyUserStorageCapacitySharedPort<br/>(storage 구현)
    participant Limit as LoadUserStorageLimitSharedPort<br/>(membership 구현)
    participant Usage as LoadUserStorageUsageUseCase (storage)

    File->>Verify: verifyCapacityFor(memberId, 요청합계 bytes)
    Verify->>Limit: findStorageLimitBytes(memberId)
    Limit-->>Verify: 할당량 (스냅샷)
    Verify->>Usage: 현재 사용량 조회
    Usage-->>Verify: 사용량
    alt 요청 + 사용량 > 할당량
        Verify-->>File: STORAGE_QUOTA_EXCEEDED → 400 (저장공간 부족)
    else 충분
        Verify-->>File: 통과 → presigned 발급 진행
    end
```

---

## 흐름 2 · 사용량 반영 (file 이벤트)

이미지가 확정/고아화되면 `file` 이 이벤트를 발행하고, `storage` 가 **커밋 이후** 사용량을 증감한다.

```mermaid
sequenceDiagram
    participant File as file (ImageFileMeta)
    participant UL as ImageUploaded/OrphanedListener (storage)
    participant UC as Record / ReleaseImageStorageUsageUseCase
    participant Usage as UserStorageUsage
    participant Log as UserStorageUsageLog

    Note over File,UL: file 트랜잭션 커밋 후 (AFTER_COMMIT)
    File-->>UL: ImageUploadedEvent (또는 ImageOrphanedEvent)
    UL->>UC: execute(memberId, imageId, resourceKind, bytes) · REQUIRES_NEW
    UC->>Log: 멱등키 존재 검사<br/>(image-uploaded:{id} / image-orphaned:{id})
    alt 이미 처리됨
        UC-->>UL: 무시 (멱등)
    else 최초 처리
        UC->>Usage: increase(bytes) · 또는 decrease(bytes)
        UC->>Log: 변경 로그 기록 (before/after, changeBytes ±)
    end
```

| 이벤트 | 유스케이스 | 사용량 | 로그 사유 |
|---|---|---|---|
| `ImageUploadedEvent` | `RecordImageStorageUsageUseCase` | **증가** | `RESOURCE_CREATION` |
| `ImageOrphanedEvent` | `ReleaseImageStorageUsageUseCase` | **감소** | `RESOURCE_DELETION` |

---

## 설계 포인트

- **스냅샷**: 할당량은 `UserMembershipEntitlement` 에 부여 시점 값으로 고정 → 정책 변경이 소급되지 않는다.
- **멱등**: `UserStorageUsageLog.idempotencyKey` 에 UNIQUE 제약. 이벤트 재시도/중복 시 `DataIntegrityViolationException` 을 잡아 무시한다.
- **독립 트랜잭션**: 리스너 유스케이스는 `REQUIRES_NEW` 로 `file` 트랜잭션과 분리된다(`AFTER_COMMIT` 이후 실행).
- **엔드포인트 없음**: 이 흐름의 유스케이스는 web 노출 없이 **공용 포트·이벤트로만** 구동된다.

---

## 구조 · 헥사고날 계층

```mermaid
flowchart LR
    subgraph file[file]
        Issue[IssueImageUploadUrlUseCase]
    end
    subgraph storage[storage]
        VerifyAd[UserStorageUsageSharedQueryAdapter]
        RecUC[Record/ReleaseImageStorageUsageUseCase]
        Listener[Image Uploaded/Orphaned Listener]
        Usage[(user_storage_usages · logs)]
    end
    subgraph membership[membership]
        LimitAd[UserMembershipEntitlementSharedQueryAdapter]
        Ent[(user_membership_entitlements)]
    end

    Issue -.->|VerifyUserStorageCapacitySharedPort| VerifyAd
    VerifyAd -.->|LoadUserStorageLimitSharedPort| LimitAd
    LimitAd --> Ent
    file -. ImageUploaded/OrphanedEvent .-> Listener
    Listener --> RecUC
    RecUC --> Usage
```

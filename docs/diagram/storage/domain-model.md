# storage 도메인 모델

회원의 스토리지 **사용량**과 그 **변경 이력**을 관리한다. 용량 산정·검증 흐름은 [storage-usage.md](storage-usage.md) 참고.

## 구성

```mermaid
flowchart TB
    subgraph d[storage.domain]
        U["UserStorageUsage<br/>현재 사용량 usedQuotaBytes"]
        L["UserStorageUsageLog<br/>변경 이력 · idempotencyKey"]
        RK{{"ResourceKind · VO"}}
        CR{{"UsageChangeReason · VO"}}
        U -. 이력 추적 .-> L
    end
```

> `UserStorageUsage` ↔ `UserStorageUsageLog` 는 코드상 명시적 1:N 연관이 아니라, 로그를 **조회**해 사용량을 추적하는 관계다.

## 애그리거트 · 불변식

| 애그리거트 | 역할 | 불변식 |
|---|---|---|
| `UserStorageUsage` | 회원별 현재 사용량 집계 | `usedQuotaBytes ≥ 0` (음수 금지, `decrease()` 시 부족하면 예외). `createForMember()`·`increase()`·`decrease()` |
| `UserStorageUsageLog` | 변경 이력(증감·사유·전후 잔액) | `idempotencyKey` UNIQUE → **멱등**. `createForResourceCreation()`·`createForResourceDeletion()` |

## 값 객체

| VO / enum | 값 |
|---|---|
| `ResourceKind` | `PORTFOLIO_CONTENT` · `PORTFOLIO_THUMBNAIL` · `RESUME_CONTENT` · `RESUME_ATTACHMENT` |
| `UsageChangeReason` | `RESOURCE_CREATION` · `RESOURCE_REPLACEMENT` · `RESOURCE_DELETION` |

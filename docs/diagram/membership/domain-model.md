# membership 도메인 모델

회원에게 부여된 **멤버십 권리**를 관리한다. 부여 시점의 정책을 **스냅샷**으로 보관해 이후 정책 변경에 영향받지 않는다. 할당량 사용 흐름은 [../storage/storage-usage.md](../storage/storage-usage.md) 참고.

## 구성 (클래스 다이어그램)

```mermaid
classDiagram
    class UserMembershipEntitlement {
        <<Aggregate Root>>
    }
    class UserMembershipPolicy {
        <<Aggregate Root>>
    }
    note for UserMembershipPolicy "policy BC (외부)"
    UserMembershipEntitlement --> UserMembershipPolicy : userMembershipPolicyId
```

> 공통 `AuditingInfo` 는 생략한다.
> `UserMembershipPolicy`는 policy BC의 애그리거트로 `userMembershipPolicyId`(id)로만 참조한다. 부여 시점의 정책 값(할당량·이름·내용)은 Entitlement의 별도 필드로 **복사·고정**되므로(스냅샷), 원본 정책이 바뀌어도 영향받지 않는다.

## 애그리거트

| 애그리거트 | 역할 | 특징 |
|---|---|---|
| `UserMembershipEntitlement` | 회원별 멤버십 권리(상태·유효기간·할당량 스냅샷) | `storageQuotaBytesSnapshot`·`membershipName/ContentSnapshot` 을 부여 시점 값으로 고정 |

## 값 객체 · 상태

| VO / enum | 값 |
|---|---|
| `MembershipStatus` | `ACTIVE` · `EXPIRED` · `REVOKED` (상태 전이 로직은 현재 없음) |
| `AssignmentReason` | `PAYMENT` · `FREE` |

> 현재는 **데이터 모델 중심**(상태 전이 메서드·도메인 이벤트 미구현)이다.

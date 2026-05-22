package io.hirecore.hirecorememberserver.modules.membership.adapter.out.persistence.jpa.entity;

import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.AbstractPersistableAggregateRoot;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.AuditingJpaInfo;
import io.hirecore.hirecorememberserver.modules.membership.domain.vo.AssignmentReason;
import io.hirecore.hirecorememberserver.modules.membership.domain.vo.MembershipStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.Instant;

@Entity
@Table(name = "user_membership_entitlements")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class UserMembershipEntitlementJpaEntity extends AbstractPersistableAggregateRoot<Long> {
    @Id
    @Comment("사용자 멤버십 권한 아이디")
    private Long id;

    @Comment("회원 계정 아이디")
    @Column(name = "member_account_id", nullable = false)
    private Long memberAccountId;

    @Comment("어떤 멤버십 정책을 기준으로 생성되었는지 나타내는 출처 식별자")
    @Column(name = "user_membership_policy_id", nullable = false)
    private Long userMembershipPolicyId;

    @Comment("멤버십 상태(활성화 여부)")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MembershipStatus status;

    @Comment("권리 부여 시점의 스토리지 바이트 용량")
    @Column(name = "storage_quota_bytes_snapshot", nullable = false)
    private Long storageQuotaBytesSnapshot;

    @Comment("권리 부여 시점의 멤버십 이름")
    @Column(name = "membership_name_snapshot", nullable = false)
    private String membershipNameSnapshot;

    @Comment("권리 부여 시점의 멤버십 설명")
    @Column(name = "membership_content_snapshot", nullable = false, columnDefinition = "TEXT")
    private String membershipContentSnapshot;

    @Comment("권리 부여 시점의 할당 사유")
    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_reason", nullable = false)
    private AssignmentReason assignmentReason;

    @Embedded
    private AuditingJpaInfo auditingInfo;

    @Comment("멤버십 시작 일시")
    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Comment("멤버십 만료 예정 일시")
    @Column(name = "expired_at")
    private Instant expiredAt;
}

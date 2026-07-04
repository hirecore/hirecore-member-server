package io.hirecore.hirecorememberserver.modules.membership.domain;

import io.hirecore.hirecorememberserver.modules.membership.domain.vo.AssignmentReason;
import io.hirecore.hirecorememberserver.modules.membership.domain.vo.MembershipStatus;
import io.hirecore.hirecorememberserver.sharedkernel.domain.AbstractDomainEventPublisher;
import io.hirecore.hirecorememberserver.sharedkernel.domain.DomainAggregateRoot;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.AuditingInfo;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

// 사용자에게 부여된 멤버십 권리 Aggregate Root (부여 시점 정책 정보를 스냅샷으로 보관)
@Getter
public class UserMembershipEntitlement extends AbstractDomainEventPublisher implements DomainAggregateRoot {
    private final Long id;
    private final Long memberAccountId;
    private final Long userMembershipPolicyId;
    private final MembershipStatus status;
    private final Long storageQuotaBytesSnapshot;
    private final String membershipNameSnapshot;
    private final String membershipContentSnapshot;
    private final AssignmentReason assignmentReason;
    private final AuditingInfo auditingInfo;
    private final Instant startedAt;
    private final Instant expiredAt;

    @Builder(access = lombok.AccessLevel.PUBLIC)
    public UserMembershipEntitlement(
            Long id,
            Long memberAccountId,
            Long userMembershipPolicyId,
            MembershipStatus status,
            Long storageQuotaBytesSnapshot,
            String membershipNameSnapshot,
            String membershipContentSnapshot,
            AssignmentReason assignmentReason,
            AuditingInfo auditingInfo,
            Instant startedAt,
            Instant expiredAt
    ) {
        this.id = id;
        this.memberAccountId = memberAccountId;
        this.userMembershipPolicyId = userMembershipPolicyId;
        this.status = status;
        this.storageQuotaBytesSnapshot = storageQuotaBytesSnapshot;
        this.membershipNameSnapshot = membershipNameSnapshot;
        this.membershipContentSnapshot = membershipContentSnapshot;
        this.assignmentReason = assignmentReason;
        this.auditingInfo = auditingInfo;
        this.startedAt = startedAt;
        this.expiredAt = expiredAt;
    }
}

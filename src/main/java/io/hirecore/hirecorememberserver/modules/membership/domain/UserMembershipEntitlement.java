package io.hirecore.hirecorememberserver.modules.membership.domain;

import io.hirecore.hirecorememberserver.modules.membership.domain.vo.AssignmentReason;
import io.hirecore.hirecorememberserver.modules.membership.domain.vo.MembershipStatus;
import io.hirecore.hirecorememberserver.sharedkernel.AbstractDomainEventPublisher;
import io.hirecore.hirecorememberserver.sharedkernel.DomainAggregateRoot;
import io.hirecore.hirecorememberserver.sharedkernel.vo.AuditingInfo;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
public class UserMembershipEntitlement extends AbstractDomainEventPublisher implements DomainAggregateRoot {
    private final Long id;
    private final Long memberAccountId;
    private final Long userMembershipPolicyId;
    private final MembershipStatus status;
    private final Long storageQuotaByteSnapshot;
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
            Long storageQuotaByteSnapshot,
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
        this.storageQuotaByteSnapshot = storageQuotaByteSnapshot;
        this.membershipNameSnapshot = membershipNameSnapshot;
        this.membershipContentSnapshot = membershipContentSnapshot;
        this.assignmentReason = assignmentReason;
        this.auditingInfo = auditingInfo;
        this.startedAt = startedAt;
        this.expiredAt = expiredAt;
    }
}

package io.hirecore.hirecorememberserver.modules.membership.domain;

import io.hirecore.hirecorememberserver.modules.membership.domain.vo.AssignmentReason;
import io.hirecore.hirecorememberserver.modules.membership.domain.vo.MembershipStatus;
import io.hirecore.hirecorememberserver.sharedkernel.domain.AbstractDomainEventPublisher;
import io.hirecore.hirecorememberserver.sharedkernel.domain.DomainAggregateRoot;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.AuditingInfo;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * 사용자에게 실제로 부여된 멤버십 권리(Entitlement)를 나타내는 Aggregate Root입니다.
 *
 * <p>이 객체는 단순히 "어떤 정책을 참조하는가"만 보관하지 않고,
 * 권리 부여 시점의 멤버십 주요 정보(스토리지 용량, 이름, 설명)를
 * 스냅샷으로 함께 유지합니다.</p>
 *
 * <p>이를 통해 이후 멤버십 정책이 변경되더라도,
 * 사용자에게 과거 어떤 조건으로 권리가 부여되었는지 추적할 수 있습니다.</p>
 *
 * <p>즉, 이 객체는 다음 두 가지를 함께 표현합니다.</p>
 * <ul>
 *     <li>사용자에게 어떤 멤버십 권리가 부여되었는가</li>
 *     <li>그 권리가 어떤 상태와 기간으로 유효한가</li>
 * </ul>
 */
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

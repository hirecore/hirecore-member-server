package io.hirecore.hirecorememberserver.modules.policy.domain;

import io.hirecore.hirecorememberserver.common.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.modules.policy.domain.exception.UserMembershipPolicyDomainException;
import io.hirecore.hirecorememberserver.modules.policy.domain.exception.UserMembershipPolicyDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.AbstractDomainEventPublisher;
import io.hirecore.hirecorememberserver.sharedkernel.DomainAggregateRoot;
import io.hirecore.hirecorememberserver.sharedkernel.vo.AuditingInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserMembershipPolicy extends AbstractDomainEventPublisher implements DomainAggregateRoot {
    private final Long id;
    private final String name;
    private final String content;
    private final Long amount;
    private final Long storageQuotaByte;
    private final Boolean active;
    private final AuditingInfo auditingInfo;

    @Builder(access = AccessLevel.PUBLIC)
    private UserMembershipPolicy(
            Long id,
            String name,
            String content,
            Long amount,
            Long storageQuotaByte,
            Boolean active,
            AuditingInfo auditingInfo
    ){
        ensureInvariants(id, name, content, amount, storageQuotaByte, active, auditingInfo);

        this.id = id;
        this.name = name;
        this.content = content;
        this.amount = amount;
        this.storageQuotaByte = storageQuotaByte;
        this.active = active;
        this.auditingInfo = auditingInfo;
    }

    private static void ensureInvariants(
            Long id,
            String name,
            String content,
            Long amount,
            Long storageQuotaByte,
            Boolean active,
            AuditingInfo auditingInfo
    ) {
        AssertionUtils.notNull(
                id,
                UserMembershipPolicyDomainExceptionCodeCluster.HiddenDetailResponse.ID_MISSING,
                UserMembershipPolicyDomainException::new
        );
        AssertionUtils.notBlank(
                name,
                UserMembershipPolicyDomainExceptionCodeCluster.HiddenDetailResponse.NAME_MISSING,
                UserMembershipPolicyDomainException::new
        );
        AssertionUtils.notBlank(
                content,
                UserMembershipPolicyDomainExceptionCodeCluster.HiddenDetailResponse.CONTENT_MISSING,
                UserMembershipPolicyDomainException::new
        );
        AssertionUtils.notNull(
                amount,
                UserMembershipPolicyDomainExceptionCodeCluster.HiddenDetailResponse.AMOUNT_MISSING,
                UserMembershipPolicyDomainException::new
        );
        AssertionUtils.notNull(
                storageQuotaByte,
                UserMembershipPolicyDomainExceptionCodeCluster.HiddenDetailResponse.STORAGE_QUOTA_BYTE_MISSING,
                UserMembershipPolicyDomainException::new
        );
        AssertionUtils.notNull(
                active,
                UserMembershipPolicyDomainExceptionCodeCluster.HiddenDetailResponse.ACTIVE_MISSING,
                UserMembershipPolicyDomainException::new
        );
        AssertionUtils.notNull(
                auditingInfo,
                UserMembershipPolicyDomainExceptionCodeCluster.HiddenDetailResponse.AUDITING_INFO_MISSING,
                UserMembershipPolicyDomainException::new
        );
    }
}

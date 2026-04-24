package io.hirecore.hirecorememberserver.modules.policy.domain;

import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.policy.domain.exception.UserMembershipPolicyDomainException;
import io.hirecore.hirecorememberserver.modules.policy.domain.exception.UserMembershipPolicyDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.domain.AbstractDomainEventPublisher;
import io.hirecore.hirecorememberserver.sharedkernel.domain.DomainAggregateRoot;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.AuditingInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserMembershipPolicy extends AbstractDomainEventPublisher implements DomainAggregateRoot {
    private final Long id;
    private final String code;
    private final String name;
    private final String content;
    private final Long amount;
    private final Long storageQuotaBytes;
    private final Boolean active;
    private final AuditingInfo auditingInfo;

    @Builder(access = AccessLevel.PUBLIC)
    private UserMembershipPolicy(
            Long id,
            String code,
            String name,
            String content,
            Long amount,
            Long storageQuotaBytes,
            Boolean active,
            AuditingInfo auditingInfo
    ){
        ensureInvariants(id, code, name, content, amount, storageQuotaBytes, active, auditingInfo);

        this.id = id;
        this.code = code;
        this.name = name;
        this.content = content;
        this.amount = amount;
        this.storageQuotaBytes = storageQuotaBytes;
        this.active = active;
        this.auditingInfo = auditingInfo;
    }

    private static void ensureInvariants(
            Long id,
            String code,
            String name,
            String content,
            Long amount,
            Long storageQuotaBytes,
            Boolean active,
            AuditingInfo auditingInfo
    ) {
        AssertionUtils.notNull(
                id,
                UserMembershipPolicyDomainExceptionCodeCluster.HiddenDetailResponse.ID_MISSING,
                UserMembershipPolicyDomainException::new
        );
        AssertionUtils.notNull(
                code,
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
                storageQuotaBytes,
                UserMembershipPolicyDomainExceptionCodeCluster.HiddenDetailResponse.STORAGE_QUOTA_BYTES_MISSING,
                UserMembershipPolicyDomainException::new
        );
        AssertionUtils.notNull(
                active,
                UserMembershipPolicyDomainExceptionCodeCluster.HiddenDetailResponse.ACTIVE_MISSING,
                UserMembershipPolicyDomainException::new
        );
        AssertionUtils.notNull(
                auditingInfo,
                SharedKernelExceptionCodeCluster.HiddenDetailResponse.AUDITING_MISSING,
                UserMembershipPolicyDomainException::new
        );
    }
}

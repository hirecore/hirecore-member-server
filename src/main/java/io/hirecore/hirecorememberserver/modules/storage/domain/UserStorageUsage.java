package io.hirecore.hirecorememberserver.modules.storage.domain;

import io.hirecore.hirecorememberserver.common.domain.exception.DomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.common.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.modules.storage.domain.exception.UserStorageUsageDomainException;
import io.hirecore.hirecorememberserver.modules.storage.domain.exception.UserStorageUsageDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.AbstractDomainEventPublisher;
import io.hirecore.hirecorememberserver.sharedkernel.DomainAggregateRoot;
import io.hirecore.hirecorememberserver.sharedkernel.vo.AuditingInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserStorageUsage extends AbstractDomainEventPublisher implements DomainAggregateRoot {
    private final Long id;
    private final Long version;
    private final Long memberAccountId;
    private final Long usedQuotaByte;
    private final AuditingInfo auditingInfo;

    @Builder(access = AccessLevel.PUBLIC)
    private UserStorageUsage(
            Long id,
            Long version,
            Long memberAccountId,
            Long usedQuotaByte,
            AuditingInfo auditingInfo
    ) {
        ensureInvariants(id, memberAccountId, usedQuotaByte, auditingInfo);

        this.id = id;
        this.version = version;
        this.memberAccountId = memberAccountId;
        this.usedQuotaByte = usedQuotaByte;
        this.auditingInfo = auditingInfo;
    }

    private static void ensureInvariants(
            Long id,
            Long memberAccountId,
            Long usedQuotaByte,
            AuditingInfo auditingInfo
    ) {
        AssertionUtils.notNull(
                id,
                UserStorageUsageDomainExceptionCodeCluster.HiddenDetailResponse.ID_MISSING,
                UserStorageUsageDomainException::new
        );
        AssertionUtils.notNull(
                memberAccountId,
                UserStorageUsageDomainExceptionCodeCluster.HiddenDetailResponse.MEMBER_ACCOUNT_ID_MISSING,
                UserStorageUsageDomainException::new
        );
        AssertionUtils.notNull(
                usedQuotaByte,
                UserStorageUsageDomainExceptionCodeCluster.HiddenDetailResponse.USED_QUOTA_BYTE_MISSING,
                UserStorageUsageDomainException::new
        );
        AssertionUtils.isTrue(
                usedQuotaByte >= 0,
                UserStorageUsageDomainExceptionCodeCluster.HiddenDetailResponse.USED_QUOTA_BYTE_NEGATIVE,
                UserStorageUsageDomainException::new
        );
        AssertionUtils.notNull(
                auditingInfo,
                DomainExceptionCodeCluster.HiddenDetailResponse.AUDITING_MISSING,
                UserStorageUsageDomainException::new
        );
    }
}

package io.hirecore.hirecorememberserver.modules.storage.domain;

import com.github.f4b6a3.tsid.TsidCreator;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.modules.storage.domain.exception.UserStorageUsageDomainException;
import io.hirecore.hirecorememberserver.modules.storage.domain.exception.UserStorageUsageDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.domain.AbstractDomainEventPublisher;
import io.hirecore.hirecorememberserver.sharedkernel.domain.DomainAggregateRoot;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.AuditingInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserStorageUsage extends AbstractDomainEventPublisher implements DomainAggregateRoot {
    private final Long id;
    private final Long version;
    private final Long memberAccountId;
    private final Long usedQuotaBytes;
    private final AuditingInfo auditingInfo;

    @Builder(access = AccessLevel.PUBLIC)
    private UserStorageUsage(
            Long id,
            Long version,
            Long memberAccountId,
            Long usedQuotaBytes,
            AuditingInfo auditingInfo
    ) {
        ensureInvariants(id, memberAccountId, usedQuotaBytes, auditingInfo);

        this.id = id;
        this.version = version;
        this.memberAccountId = memberAccountId;
        this.usedQuotaBytes = usedQuotaBytes;
        this.auditingInfo = auditingInfo;
    }

    public static UserStorageUsage createForMember(Long memberAccountId) {
        return UserStorageUsage.builder()
                .id(TsidCreator.getTsid().toLong())
                .memberAccountId(memberAccountId)
                .usedQuotaBytes(0L)
                .auditingInfo(AuditingInfo.create())
                .build();
    }

    public UserStorageUsage increase(Long bytes) {
        AssertionUtils.notNull(
                bytes,
                UserStorageUsageDomainExceptionCodeCluster.HiddenDetailResponse.USED_QUOTA_BYTES_MISSING,
                UserStorageUsageDomainException::new
        );
        AssertionUtils.isTrue(
                bytes >= 0,
                UserStorageUsageDomainExceptionCodeCluster.HiddenDetailResponse.USED_QUOTA_BYTES_NEGATIVE,
                UserStorageUsageDomainException::new
        );

        return UserStorageUsage.builder()
                .id(this.id)
                .version(this.version)
                .memberAccountId(this.memberAccountId)
                .usedQuotaBytes(this.usedQuotaBytes + bytes)
                .auditingInfo(this.auditingInfo.update())
                .build();
    }

    private static void ensureInvariants(
            Long id,
            Long memberAccountId,
            Long usedQuotaBytes,
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
                usedQuotaBytes,
                UserStorageUsageDomainExceptionCodeCluster.HiddenDetailResponse.USED_QUOTA_BYTES_MISSING,
                UserStorageUsageDomainException::new
        );
        AssertionUtils.isTrue(
                usedQuotaBytes >= 0,
                UserStorageUsageDomainExceptionCodeCluster.HiddenDetailResponse.USED_QUOTA_BYTES_NEGATIVE,
                UserStorageUsageDomainException::new
        );
        AssertionUtils.notNull(
                auditingInfo,
                SharedKernelExceptionCodeCluster.HiddenDetailResponse.AUDITING_MISSING,
                UserStorageUsageDomainException::new
        );
    }
}

package io.hirecore.hirecorememberserver.modules.storage.domain;

import com.github.f4b6a3.tsid.TsidCreator;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.modules.storage.domain.exception.UserStorageUsageLogDomainException;
import io.hirecore.hirecorememberserver.modules.storage.domain.exception.UserStorageUsageLogDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.storage.domain.vo.UsageChangeReason;
import io.hirecore.hirecorememberserver.modules.storage.domain.vo.ResourceKind;
import io.hirecore.hirecorememberserver.sharedkernel.domain.AbstractDomainEventPublisher;
import io.hirecore.hirecorememberserver.sharedkernel.domain.DomainAggregateRoot;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.AuditingInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

// 스토리지 사용량 변경 이력 (증감/전후 총량 보관, 멱등 보장)
@Getter
public class UserStorageUsageLog extends AbstractDomainEventPublisher implements DomainAggregateRoot {

    private final Long id;

    private final Long memberAccountId;

    private final UsageChangeReason usageChangeReason;

    private final ResourceKind resourceKind;

    private final Long resourceKindId;

    // 증감량(byte): 양수 증가, 음수 감소
    private final Long changeBytes;

    private final Long beforeUsedQuotaBytes;

    private final Long afterUsedQuotaBytes;

    // 멱등 키: 같은 요청 중복 반영 방지
    private final String idempotencyKey;

    private final AuditingInfo auditingInfo;

    @Builder(access = AccessLevel.PUBLIC)
    private UserStorageUsageLog(
            Long id,
            Long memberAccountId,
            UsageChangeReason usageChangeReason,
            ResourceKind resourceKind,
            Long resourceKindId,
            Long changeBytes,
            Long beforeUsedQuotaBytes,
            Long afterUsedQuotaBytes,
            String idempotencyKey,
            AuditingInfo auditingInfo
    ) {
        ensureInvariants(
                id, memberAccountId, usageChangeReason, resourceKind,
                resourceKindId, changeBytes, beforeUsedQuotaBytes,
                afterUsedQuotaBytes, idempotencyKey, auditingInfo
        );

        this.id = id;
        this.memberAccountId = memberAccountId;
        this.usageChangeReason = usageChangeReason;
        this.resourceKind = resourceKind;
        this.resourceKindId = resourceKindId;
        this.changeBytes = changeBytes;
        this.beforeUsedQuotaBytes = beforeUsedQuotaBytes;
        this.afterUsedQuotaBytes = afterUsedQuotaBytes;
        this.idempotencyKey = idempotencyKey;
        this.auditingInfo = auditingInfo;
    }

    public static UserStorageUsageLog createForResourceCreation(
            Long memberAccountId,
            ResourceKind resourceKind,
            Long resourceKindId,
            Long changeBytes,
            Long beforeUsedQuotaBytes,
            Long afterUsedQuotaBytes,
            String idempotencyKey
    ) {
        return UserStorageUsageLog.builder()
                .id(TsidCreator.getTsid().toLong())
                .memberAccountId(memberAccountId)
                .usageChangeReason(UsageChangeReason.RESOURCE_CREATION)
                .resourceKind(resourceKind)
                .resourceKindId(resourceKindId)
                .changeBytes(changeBytes)
                .beforeUsedQuotaBytes(beforeUsedQuotaBytes)
                .afterUsedQuotaBytes(afterUsedQuotaBytes)
                .idempotencyKey(idempotencyKey)
                .auditingInfo(AuditingInfo.create())
                .build();
    }

    // 사용량 차감 이력: changeBytes 는 음수로 전달
    public static UserStorageUsageLog createForResourceDeletion(
            Long memberAccountId,
            ResourceKind resourceKind,
            Long resourceKindId,
            Long changeBytes,
            Long beforeUsedQuotaBytes,
            Long afterUsedQuotaBytes,
            String idempotencyKey
    ) {
        return UserStorageUsageLog.builder()
                .id(TsidCreator.getTsid().toLong())
                .memberAccountId(memberAccountId)
                .usageChangeReason(UsageChangeReason.RESOURCE_DELETION)
                .resourceKind(resourceKind)
                .resourceKindId(resourceKindId)
                .changeBytes(changeBytes)
                .beforeUsedQuotaBytes(beforeUsedQuotaBytes)
                .afterUsedQuotaBytes(afterUsedQuotaBytes)
                .idempotencyKey(idempotencyKey)
                .auditingInfo(AuditingInfo.create())
                .build();
    }

    private static void ensureInvariants(
            Long id,
            Long memberAccountId,
            UsageChangeReason usageChangeReason,
            ResourceKind resourceKind,
            Long resourceKindId,
            Long changeBytes,
            Long beforeUsedQuotaBytes,
            Long afterUsedQuotaBytes,
            String idempotencyKey,
            AuditingInfo auditingInfo
    ) {
        AssertionUtils.notNull(
                id,
                UserStorageUsageLogDomainExceptionCodeCluster.HiddenDetailResponse.ID_MISSING,
                UserStorageUsageLogDomainException::new
        );
        AssertionUtils.notNull(
                memberAccountId,
                UserStorageUsageLogDomainExceptionCodeCluster.HiddenDetailResponse.MEMBER_ACCOUNT_ID_MISSING,
                UserStorageUsageLogDomainException::new
        );
        AssertionUtils.notNull(
                usageChangeReason,
                UserStorageUsageLogDomainExceptionCodeCluster.HiddenDetailResponse.USAGE_CHANGE_REASON_MISSING,
                UserStorageUsageLogDomainException::new
        );
        AssertionUtils.notNull(
                resourceKind,
                UserStorageUsageLogDomainExceptionCodeCluster.HiddenDetailResponse.RESOURCE_KIND_MISSING,
                UserStorageUsageLogDomainException::new
        );
        AssertionUtils.notNull(
                resourceKindId,
                UserStorageUsageLogDomainExceptionCodeCluster.HiddenDetailResponse.RESOURCE_KIND_ID_MISSING,
                UserStorageUsageLogDomainException::new
        );
        AssertionUtils.notNull(
                changeBytes,
                UserStorageUsageLogDomainExceptionCodeCluster.HiddenDetailResponse.CHANGE_BYTES_MISSING,
                UserStorageUsageLogDomainException::new
        );
        AssertionUtils.notNull(
                beforeUsedQuotaBytes,
                UserStorageUsageLogDomainExceptionCodeCluster.HiddenDetailResponse.BEFORE_USED_QUOTA_BYTES_MISSING,
                UserStorageUsageLogDomainException::new
        );
        AssertionUtils.isTrue(
                beforeUsedQuotaBytes >= 0,
                UserStorageUsageLogDomainExceptionCodeCluster.HiddenDetailResponse.BEFORE_USED_QUOTA_BYTES_NEGATIVE,
                UserStorageUsageLogDomainException::new
        );
        AssertionUtils.notNull(
                afterUsedQuotaBytes,
                UserStorageUsageLogDomainExceptionCodeCluster.HiddenDetailResponse.AFTER_USED_QUOTA_BYTES_MISSING,
                UserStorageUsageLogDomainException::new
        );
        AssertionUtils.isTrue(
                afterUsedQuotaBytes >= 0,
                UserStorageUsageLogDomainExceptionCodeCluster.HiddenDetailResponse.AFTER_USED_QUOTA_BYTES_NEGATIVE,
                UserStorageUsageLogDomainException::new
        );
        AssertionUtils.notBlank(
                idempotencyKey,
                UserStorageUsageLogDomainExceptionCodeCluster.HiddenDetailResponse.IDEMPOTENCY_KEY_MISSING,
                UserStorageUsageLogDomainException::new
        );
        AssertionUtils.notNull(
                auditingInfo,
                SharedKernelExceptionCodeCluster.HiddenDetailResponse.AUDITING_MISSING,
                UserStorageUsageLogDomainException::new
        );
    }
}
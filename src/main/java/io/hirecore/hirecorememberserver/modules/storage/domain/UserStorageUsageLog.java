package io.hirecore.hirecorememberserver.modules.storage.domain;

import io.hirecore.hirecorememberserver.common.domain.exception.DomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.common.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.modules.storage.domain.exception.UserStorageUsageLogDomainException;
import io.hirecore.hirecorememberserver.modules.storage.domain.exception.UserStorageUsageLogDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.storage.domain.vo.UsageChangeReason;
import io.hirecore.hirecorememberserver.modules.storage.domain.vo.ResourceKind;
import io.hirecore.hirecorememberserver.sharedkernel.vo.AuditingInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

/**
 * 사용자 스토리지 사용량의 변경 이력을 나타내는 도메인 객체입니다.
 *
 * <p>이 객체는 특정 사용자의 스토리지 사용량이 어떤 행위로 인해 얼마나 변경되었는지와,
 * 변경 전/후 총 사용량을 함께 보관하기 위한 목적을 가집니다.</p>
 *
 * <p>주요 활용 목적은 다음과 같습니다.</p>
 * <ul>
 *     <li>사용량 증가/감소 이력 추적</li>
 *     <li>변경 원인 리소스 식별</li>
 *     <li>변경 전/후 총 사용량 검증</li>
 *     <li>중복 처리 방지를 위한 idempotency 보장</li>
 * </ul>
 */
@Getter
public class UserStorageUsageLog {

    /** 사용량 변경 이력 식별자입니다. */
    private final Long id;

    /** 사용량이 귀속되는 회원 계정 식별자입니다. */
    private final Long memberAccountId;

    /** 사용량 변화를 발생시킨 이유입니다. */
    private final UsageChangeReason usageChangeReason;

    /** 사용량 변경을 발생시킨 리소스(자원) 종류입니다. */
    private final ResourceKind resourceKind;

    /** 사용량 변경 대상 리소스(자원) 식별자입니다. */
    private final Long resourceKindId;

    /**
     * 이번 변경으로 증감된 사용량(byte)입니다.
     *
     * <p>양수는 증가, 음수는 감소를 의미합니다.</p>
     */
    private final Long changeByte;

    /** 변경 반영 직전의 총 사용량(byte)입니다. */
    private final Long beforeUsedQuotaByte;

    /** 변경 반영 직후의 총 사용량(byte)입니다. */
    private final Long afterUsedQuotaByte;

    /**
     * 동일 요청의 중복 반영을 방지하기 위한 멱등 키입니다.
     *
     * <p>같은 비즈니스 요청은 동일한 키를 사용해야 합니다.</p>
     */
    private final String idempotencyKey;

    /** 생성/수정 시점 등의 감사 정보입니다. */
    private final AuditingInfo auditingInfo;

    /**
     * 사용자 스토리지 사용량 변경 이력을 생성합니다.
     *
     * @param id 사용량 변경 이력 식별자
     * @param memberAccountId 사용량이 귀속되는 회원 계정 식별자
     * @param usageChangeReason 사용량 변경을 발생시킨 행위 유형
     * @param resourceKind 사용량 변경 대상 리소스 유형
     * @param resourceKindId 사용량 변경 대상 리소스 식별자
     * @param changeByte 이번 변경으로 증감된 사용량(byte)
     * @param beforeUsedQuotaByte 변경 반영 직전 총 사용량(byte)
     * @param afterUsedQuotaByte 변경 반영 직후 총 사용량(byte)
     * @param idempotencyKey 동일 요청 중복 반영 방지 키
     * @param auditingInfo 감사 정보
     */
    @Builder(access = AccessLevel.PUBLIC)
    private UserStorageUsageLog(
            Long id,
            Long memberAccountId,
            UsageChangeReason usageChangeReason,
            ResourceKind resourceKind,
            Long resourceKindId,
            Long changeByte,
            Long beforeUsedQuotaByte,
            Long afterUsedQuotaByte,
            String idempotencyKey,
            AuditingInfo auditingInfo
    ) {
        ensureInvariants(
                id, memberAccountId, usageChangeReason, resourceKind,
                resourceKindId, changeByte, beforeUsedQuotaByte,
                afterUsedQuotaByte, idempotencyKey, auditingInfo
        );

        this.id = id;
        this.memberAccountId = memberAccountId;
        this.usageChangeReason = usageChangeReason;
        this.resourceKind = resourceKind;
        this.resourceKindId = resourceKindId;
        this.changeByte = changeByte;
        this.beforeUsedQuotaByte = beforeUsedQuotaByte;
        this.afterUsedQuotaByte = afterUsedQuotaByte;
        this.idempotencyKey = idempotencyKey;
        this.auditingInfo = auditingInfo;
    }

    private static void ensureInvariants(
            Long id,
            Long memberAccountId,
            UsageChangeReason usageChangeReason,
            ResourceKind resourceKind,
            Long resourceKindId,
            Long changeByte,
            Long beforeUsedQuotaByte,
            Long afterUsedQuotaByte,
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
                changeByte,
                UserStorageUsageLogDomainExceptionCodeCluster.HiddenDetailResponse.CHANGE_BYTE_MISSING,
                UserStorageUsageLogDomainException::new
        );
        AssertionUtils.notNull(
                beforeUsedQuotaByte,
                UserStorageUsageLogDomainExceptionCodeCluster.HiddenDetailResponse.BEFORE_USED_QUOTA_BYTE_MISSING,
                UserStorageUsageLogDomainException::new
        );
        AssertionUtils.isTrue(
                beforeUsedQuotaByte >= 0,
                UserStorageUsageLogDomainExceptionCodeCluster.HiddenDetailResponse.BEFORE_USED_QUOTA_BYTE_NEGATIVE,
                UserStorageUsageLogDomainException::new
        );
        AssertionUtils.notNull(
                afterUsedQuotaByte,
                UserStorageUsageLogDomainExceptionCodeCluster.HiddenDetailResponse.AFTER_USED_QUOTA_BYTE_MISSING,
                UserStorageUsageLogDomainException::new
        );
        AssertionUtils.isTrue(
                afterUsedQuotaByte >= 0,
                UserStorageUsageLogDomainExceptionCodeCluster.HiddenDetailResponse.AFTER_USED_QUOTA_BYTE_NEGATIVE,
                UserStorageUsageLogDomainException::new
        );
        AssertionUtils.notBlank(
                idempotencyKey,
                UserStorageUsageLogDomainExceptionCodeCluster.HiddenDetailResponse.IDEMPOTENCY_KEY_MISSING,
                UserStorageUsageLogDomainException::new
        );
        AssertionUtils.notNull(
                auditingInfo,
                DomainExceptionCodeCluster.HiddenDetailResponse.AUDITING_MISSING,
                UserStorageUsageLogDomainException::new
        );
    }
}
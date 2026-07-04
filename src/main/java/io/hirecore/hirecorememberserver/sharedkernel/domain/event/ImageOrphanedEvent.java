package io.hirecore.hirecorememberserver.sharedkernel.domain.event;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelExceptionCodeCluster.HiddenDetailResponse;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.DomainType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Purpose;

import java.time.Instant;

// 이미지 UPLOADED→ORPHANED 전이 시 발행 (스토리지 사용량 회수 등)
public record ImageOrphanedEvent(
        Long imageFileMetaId,
        Long memberAccountId,
        DomainType domainType,
        Purpose purpose,
        Long fileSizeBytes,
        Instant orphanedAt
) {
    public ImageOrphanedEvent {
        AssertionUtils.notNull(imageFileMetaId, HiddenDetailResponse.IMAGE_ORPHANED_EVENT_IMAGE_FILE_META_ID_MISSING, SharedKernelException::new);
        AssertionUtils.notNull(memberAccountId, HiddenDetailResponse.IMAGE_ORPHANED_EVENT_MEMBER_ACCOUNT_ID_MISSING, SharedKernelException::new);
        AssertionUtils.notNull(domainType, HiddenDetailResponse.IMAGE_ORPHANED_EVENT_DOMAIN_TYPE_MISSING, SharedKernelException::new);
        AssertionUtils.notNull(purpose, HiddenDetailResponse.IMAGE_ORPHANED_EVENT_PURPOSE_MISSING, SharedKernelException::new);
        AssertionUtils.notNull(fileSizeBytes, HiddenDetailResponse.IMAGE_ORPHANED_EVENT_FILE_SIZE_BYTES_MISSING, SharedKernelException::new);
        AssertionUtils.notNull(orphanedAt, HiddenDetailResponse.IMAGE_ORPHANED_EVENT_ORPHANED_AT_MISSING, SharedKernelException::new);
    }
}

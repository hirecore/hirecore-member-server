package io.hirecore.hirecorememberserver.sharedkernel.domain.event;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelExceptionCodeCluster.HiddenDetailResponse;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.DomainType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Purpose;

import java.time.Instant;

// 이미지 PENDING→UPLOADED 전이 시 발행 (스토리지 사용량 갱신 등)
public record ImageUploadedEvent(
        Long imageFileMetaId,
        Long memberAccountId,
        DomainType domainType,
        Purpose purpose,
        Long fileSizeBytes,
        Instant completedUploadAt
) {
    public ImageUploadedEvent {
        AssertionUtils.notNull(imageFileMetaId, HiddenDetailResponse.IMAGE_UPLOADED_EVENT_IMAGE_FILE_META_ID_MISSING, SharedKernelException::new);
        AssertionUtils.notNull(memberAccountId, HiddenDetailResponse.IMAGE_UPLOADED_EVENT_MEMBER_ACCOUNT_ID_MISSING, SharedKernelException::new);
        AssertionUtils.notNull(domainType, HiddenDetailResponse.IMAGE_UPLOADED_EVENT_DOMAIN_TYPE_MISSING, SharedKernelException::new);
        AssertionUtils.notNull(purpose, HiddenDetailResponse.IMAGE_UPLOADED_EVENT_PURPOSE_MISSING, SharedKernelException::new);
        AssertionUtils.notNull(fileSizeBytes, HiddenDetailResponse.IMAGE_UPLOADED_EVENT_FILE_SIZE_BYTES_MISSING, SharedKernelException::new);
        AssertionUtils.notNull(completedUploadAt, HiddenDetailResponse.IMAGE_UPLOADED_EVENT_COMPLETED_UPLOAD_AT_MISSING, SharedKernelException::new);
    }
}

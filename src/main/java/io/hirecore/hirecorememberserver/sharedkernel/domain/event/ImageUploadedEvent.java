package io.hirecore.hirecorememberserver.sharedkernel.domain.event;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelExceptionCodeCluster.HiddenDetailResponse;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.DomainType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Purpose;

import java.time.Instant;

/**
 * 이미지 파일 메타가 PENDING 상태에서 UPLOADED 상태로 전이되었을 때 발행되는 도메인 이벤트입니다.
 *
 * <p>{@code ImageFileMeta} Aggregate Root가 업로드 확정 시점에 생성하며,
 * 사용자 스토리지 사용량 갱신 등 후속 BC 핸들러가 구독하여 부수효과를 처리할 수 있게 합니다.</p>
 *
 * <p>{@code domainType} 과 {@code purpose} 는 publisher 측 도메인 분류 정보로, consumer 가
 * 자체 분류 체계(예: storage BC 의 {@code ResourceKind})로 해석할 때 사용합니다.</p>
 */
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

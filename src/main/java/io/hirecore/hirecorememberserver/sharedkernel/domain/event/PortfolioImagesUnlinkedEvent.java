package io.hirecore.hirecorememberserver.sharedkernel.domain.event;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelExceptionCodeCluster.HiddenDetailResponse;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;

import java.util.List;

// 포트폴리오 수정으로 참조 해제된 이미지 알림 (file BC 가 ORPHANED 전이, BEFORE_COMMIT ATOMIC)
public record PortfolioImagesUnlinkedEvent(
        Long portfolioId,
        Long memberAccountId,
        List<Long> imageFileMetaIds
) {
    public PortfolioImagesUnlinkedEvent {
        AssertionUtils.notNull(portfolioId, HiddenDetailResponse.PORTFOLIO_IMAGES_UNLINKED_EVENT_PORTFOLIO_ID_MISSING, SharedKernelException::new);
        AssertionUtils.notNull(memberAccountId, HiddenDetailResponse.PORTFOLIO_IMAGES_UNLINKED_EVENT_MEMBER_ACCOUNT_ID_MISSING, SharedKernelException::new);
        AssertionUtils.notNull(imageFileMetaIds, HiddenDetailResponse.PORTFOLIO_IMAGES_UNLINKED_EVENT_IMAGE_FILE_META_IDS_MISSING, SharedKernelException::new);
        AssertionUtils.isTrue(!imageFileMetaIds.isEmpty(), HiddenDetailResponse.PORTFOLIO_IMAGES_UNLINKED_EVENT_IMAGE_FILE_META_IDS_EMPTY, SharedKernelException::new);
        imageFileMetaIds = List.copyOf(imageFileMetaIds);
    }
}

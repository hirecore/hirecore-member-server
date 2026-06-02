package io.hirecore.hirecorememberserver.sharedkernel.domain.event;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelExceptionCodeCluster.HiddenDetailResponse;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;

import java.util.List;

/**
 * 포트폴리오 수정으로 인해 본문/썸네일에서 더 이상 참조되지 않게 된 이미지 식별자들을 알리는 이벤트입니다.
 *
 * <p>{@code Portfolio.modify(...)} 호출 시 {@code (이전 thumbnail + 이전 content) - (새 thumbnail + 새 content)}
 * 차집합이 비어있지 않은 경우에만 발행됩니다. 회수 대상이 없으면 이벤트 자체를 생략하므로
 * {@code imageFileMetaIds} 는 항상 1개 이상의 요소를 가집니다.</p>
 *
 * <p>file BC 가 구독하여 각 {@code ImageFileMeta} 를 ORPHANED 로 전이시킵니다.
 * BEFORE_COMMIT 페이즈로 구독하여 포트폴리오 수정 트랜잭션과 ATOMIC 하게 처리됩니다.</p>
 */
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

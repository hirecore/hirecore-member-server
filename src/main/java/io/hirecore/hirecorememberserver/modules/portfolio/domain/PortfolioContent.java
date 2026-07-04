package io.hirecore.hirecorememberserver.modules.portfolio.domain;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.AuditingInfo;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioContentDomainException;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioContentDomainExceptionCodeCluster;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PortfolioContent {
    // Portfolio 식별자와 동일 (JPA @MapsId)
    private final Long portfolioId;
    private final String contentJson;
    private final String contentHtml;
    // 본문에 임베드된 이미지 ID 집합 (수정 시 차집합으로 고아 이미지 회수 트리거)
    private final List<Long> imageIds;
    private final AuditingInfo auditingInfo;

    // 복원용 빌더 (인프라 조회 전용, ArchUnit으로 임의 호출 차단)
    @Builder(access = AccessLevel.PUBLIC)
    private PortfolioContent(
            Long portfolioId,
            String contentJson,
            String contentHtml,
            List<Long> imageIds,
            AuditingInfo auditingInfo
    ) {
        ensureInvariants(portfolioId, contentJson, contentHtml, imageIds, auditingInfo);

        this.portfolioId = portfolioId;
        this.contentJson = contentJson;
        this.contentHtml = contentHtml;
        this.imageIds = List.copyOf(imageIds);
        this.auditingInfo = auditingInfo;
    }

    public static PortfolioContent create(
            Long portfolioId,
            String contentJson,
            String contentHtml,
            List<Long> imageIds
    ) {
        return PortfolioContent.builder()
                .portfolioId(portfolioId)
                .contentJson(contentJson)
                .contentHtml(contentHtml)
                .imageIds(imageIds)
                .auditingInfo(AuditingInfo.create())
                .build();
    }

    private static void ensureInvariants(
            Long portfolioId,
            String contentJson,
            String contentHtml,
            List<Long> imageIds,
            AuditingInfo auditingInfo
    ) {
        AssertionUtils.notNull(
                portfolioId,
                PortfolioContentDomainExceptionCodeCluster.HiddenDetailResponse.PORTFOLIO_ID_MISSING,
                PortfolioContentDomainException::new
        );
        AssertionUtils.notBlank(
                contentJson,
                PortfolioContentDomainExceptionCodeCluster.HiddenDetailResponse.CONTENT_JSON_MISSING,
                PortfolioContentDomainException::new
        );
        AssertionUtils.notBlank(
                contentHtml,
                PortfolioContentDomainExceptionCodeCluster.HiddenDetailResponse.CONTENT_HTML_MISSING,
                PortfolioContentDomainException::new
        );
        AssertionUtils.notNull(
                imageIds,
                PortfolioContentDomainExceptionCodeCluster.HiddenDetailResponse.IMAGE_IDS_MISSING,
                PortfolioContentDomainException::new
        );
        ensureImageIdsAreClean(imageIds);
        AssertionUtils.notNull(
                auditingInfo,
                SharedKernelExceptionCodeCluster.HiddenDetailResponse.AUDITING_MISSING,
                PortfolioContentDomainException::new
        );
    }

    private static void ensureImageIdsAreClean(List<Long> imageIds) {
        for (Long imageId : imageIds) {
            if (imageId == null) {
                throw new PortfolioContentDomainException(
                        PortfolioContentDomainExceptionCodeCluster.HiddenDetailResponse.IMAGE_IDS_CONTAINS_NULL
                );
            }
        }
    }
}

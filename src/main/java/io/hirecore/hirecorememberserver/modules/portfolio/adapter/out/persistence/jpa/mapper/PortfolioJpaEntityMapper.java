package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper;

import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioJobCategoryJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioTagJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioTag;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.AuditingJpaInfo;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.AuditingInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(config = GlobalMapStructConfig.class)
public abstract class PortfolioJpaEntityMapper {

    @Autowired
    protected PortfolioContentJpaEntityMapper portfolioContentMapper;

    @Autowired
    protected PortfolioTagJpaEntityMapper portfolioTagMapper;

    @Autowired
    protected PortfolioJobCategoryJpaEntityMapper portfolioJobCategoryMapper;

    @Mapping(target = "portfolioContent", ignore = true)
    @Mapping(target = "portfolioJobCategory", ignore = true)
    @Mapping(target = "portfolioTags", ignore = true)
    public abstract PortfolioJpaEntity toJpaEntity(Portfolio domain);

    public Portfolio toDomain(PortfolioJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Portfolio.builder()
                .id(entity.getId())
                .memberAccountId(entity.getMemberAccountId())
                .thumbnailImageId(entity.getThumbnailImageId())
                .coverLetterId(entity.getCoverLetterId())
                .resumeId(entity.getResumeId())
                .title(entity.getTitle())
                .previewSummary(entity.getPreviewSummary())
                .privateMemo(entity.getPrivateMemo())
                .cachedViewCount(entity.getCachedViewCount())
                .cachedInterestCount(entity.getCachedInterestCount())
                .portfolioJobCategory(portfolioJobCategoryMapper.toDomain(entity.getPortfolioJobCategory()))
                .portfolioContent(portfolioContentMapper.toDomain(entity.getPortfolioContent()))
                .externalLinks(entity.getExternalLinks())
                .portfolioTags(toDomainTags(entity.getPortfolioTags()))
                .status(entity.getStatus())
                .collaborationType(entity.getCollaborationType())
                .visibility(entity.getVisibility())
                .auditingInfo(toAuditingInfo(entity.getAuditingInfo()))
                .build();
    }

    private List<PortfolioTag> toDomainTags(List<PortfolioTagJpaEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }
        return entities.stream()
                .map(portfolioTagMapper::toDomain)
                .toList();
    }

    private AuditingInfo toAuditingInfo(AuditingJpaInfo info) {
        if (info == null) {
            return null;
        }
        return new AuditingInfo(info.createdAt(), info.updatedAt());
    }
}

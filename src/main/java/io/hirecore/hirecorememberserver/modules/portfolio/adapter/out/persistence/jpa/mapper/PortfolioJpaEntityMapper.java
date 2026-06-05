package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper;

import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioJobCategoryJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioTagJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioTag;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.AuditingJpaInfo;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.AuditingInfo;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
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

    /**
     * 부모 엔티티 변환 직후 자식 그래프를 조립하고 양방향 연관관계를 동기화합니다.
     *
     * <p>{@link PortfolioJpaEntity#syncPortfolioContent}, {@link PortfolioJpaEntity#syncPortfolioJobCategory},
     * {@link PortfolioJpaEntity#addPortfolioTag} 헬퍼를 통해 owning side / inverse side 양쪽 참조가
     * 한 호출 안에서 일관되게 채워지도록 한다.</p>
     */
    @AfterMapping
    protected void assembleChildren(Portfolio domain, @MappingTarget PortfolioJpaEntity entity) {
        if (domain == null || entity == null) {
            return;
        }
        entity.syncPortfolioContent(portfolioContentMapper.toJpaEntity(domain.getPortfolioContent()));
        entity.syncPortfolioJobCategory(portfolioJobCategoryMapper.toJpaEntity(domain.getPortfolioJobCategory()));
        if (domain.getPortfolioTags() != null) {
            domain.getPortfolioTags().stream()
                    .map(portfolioTagMapper::toJpaEntity)
                    .forEach(entity::addPortfolioTag);
        }
    }

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

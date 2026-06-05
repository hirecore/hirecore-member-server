package io.hirecore.hirecorememberserver.modules.coverletter.adapter.out.persistence.jpa.mapper;

import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.coverletter.adapter.out.persistence.jpa.entity.CoverLetterJpaEntity;
import io.hirecore.hirecorememberserver.modules.coverletter.adapter.out.persistence.jpa.entity.CoverLetterTagJpaEntity;
import io.hirecore.hirecorememberserver.modules.coverletter.domain.CoverLetter;
import io.hirecore.hirecorememberserver.modules.coverletter.domain.CoverLetterTag;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.AuditingJpaInfo;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.AuditingInfo;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(config = GlobalMapStructConfig.class)
public abstract class CoverLetterJpaEntityMapper {

    @Autowired
    protected CoverLetterContentJpaEntityMapper coverLetterContentMapper;

    @Autowired
    protected CoverLetterTagJpaEntityMapper coverLetterTagMapper;

    @Autowired
    protected CoverLetterJobCategoryJpaEntityMapper coverLetterJobCategoryMapper;

    @Mapping(target = "coverLetterContent", ignore = true)
    @Mapping(target = "coverLetterJobCategory", ignore = true)
    @Mapping(target = "coverLetterTags", ignore = true)
    public abstract CoverLetterJpaEntity toJpaEntity(CoverLetter domain);

    /**
     * 부모 엔티티 변환 직후 자식 그래프를 조립하고 양방향 연관관계를 동기화합니다.
     *
     * <p>{@link CoverLetterJpaEntity#syncCoverLetterContent}, {@link CoverLetterJpaEntity#syncCoverLetterJobCategory},
     * {@link CoverLetterJpaEntity#addCoverLetterTag} 헬퍼를 통해 owning side / inverse side 양쪽 참조가
     * 한 호출 안에서 일관되게 채워지도록 한다.</p>
     */
    @AfterMapping
    protected void assembleChildren(CoverLetter domain, @MappingTarget CoverLetterJpaEntity entity) {
        if (domain == null || entity == null) {
            return;
        }
        entity.syncCoverLetterContent(coverLetterContentMapper.toJpaEntity(domain.getCoverLetterContent()));
        entity.syncCoverLetterJobCategory(coverLetterJobCategoryMapper.toJpaEntity(domain.getCoverLetterJobCategory()));
        if (domain.getCoverLetterTags() != null) {
            domain.getCoverLetterTags().stream()
                    .map(coverLetterTagMapper::toJpaEntity)
                    .forEach(entity::addCoverLetterTag);
        }
    }

    public CoverLetter toDomain(CoverLetterJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return CoverLetter.builder()
                .id(entity.getId())
                .memberAccountId(entity.getMemberAccountId())
                .thumbnailImageId(entity.getThumbnailImageId())
                .title(entity.getTitle())
                .previewSummary(entity.getPreviewSummary())
                .privateMemo(entity.getPrivateMemo())
                .cachedViewCount(entity.getCachedViewCount())
                .cachedInterestCount(entity.getCachedInterestCount())
                .coverLetterJobCategory(coverLetterJobCategoryMapper.toDomain(entity.getCoverLetterJobCategory()))
                .coverLetterContent(coverLetterContentMapper.toDomain(entity.getCoverLetterContent()))
                .externalLinks(entity.getExternalLinks())
                .coverLetterTags(toDomainTags(entity.getCoverLetterTags()))
                .portfolioIds(entity.getPortfolioIds() != null ? entity.getPortfolioIds() : List.of())
                .status(entity.getStatus())
                .collaborationType(entity.getCollaborationType())
                .visibility(entity.getVisibility())
                .auditingInfo(toAuditingInfo(entity.getAuditingInfo()))
                .build();
    }

    private List<CoverLetterTag> toDomainTags(List<CoverLetterTagJpaEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }
        return entities.stream()
                .map(coverLetterTagMapper::toDomain)
                .toList();
    }

    private AuditingInfo toAuditingInfo(AuditingJpaInfo info) {
        if (info == null) {
            return null;
        }
        return new AuditingInfo(info.createdAt(), info.updatedAt());
    }
}

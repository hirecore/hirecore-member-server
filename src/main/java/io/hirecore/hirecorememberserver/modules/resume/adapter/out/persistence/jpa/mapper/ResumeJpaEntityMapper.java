package io.hirecore.hirecorememberserver.modules.resume.adapter.out.persistence.jpa.mapper;

import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.resume.adapter.out.persistence.jpa.entity.ResumeJpaEntity;
import io.hirecore.hirecorememberserver.modules.resume.adapter.out.persistence.jpa.entity.ResumeTagJpaEntity;
import io.hirecore.hirecorememberserver.modules.resume.domain.Resume;
import io.hirecore.hirecorememberserver.modules.resume.domain.ResumeTag;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.AuditingJpaInfo;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.AuditingInfo;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(config = GlobalMapStructConfig.class)
public abstract class ResumeJpaEntityMapper {

    @Autowired
    protected ResumeContentJpaEntityMapper resumeContentMapper;

    @Autowired
    protected ResumeTagJpaEntityMapper resumeTagMapper;

    @Autowired
    protected ResumeJobCategoryJpaEntityMapper resumeJobCategoryMapper;

    @Mapping(target = "resumeContent", ignore = true)
    @Mapping(target = "resumeJobCategory", ignore = true)
    @Mapping(target = "resumeTags", ignore = true)
    public abstract ResumeJpaEntity toJpaEntity(Resume domain);

    // 부모 변환 직후 자식 그래프 조립 및 양방향 연관관계 동기화
    @AfterMapping
    protected void assembleChildren(Resume domain, @MappingTarget ResumeJpaEntity entity) {
        if (domain == null || entity == null) {
            return;
        }
        entity.syncResumeContent(resumeContentMapper.toJpaEntity(domain.getResumeContent()));
        entity.syncResumeJobCategory(resumeJobCategoryMapper.toJpaEntity(domain.getResumeJobCategory()));
        if (domain.getResumeTags() != null) {
            domain.getResumeTags().stream()
                    .map(resumeTagMapper::toJpaEntity)
                    .forEach(entity::addResumeTag);
        }
    }

    public Resume toDomain(ResumeJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Resume.builder()
                .id(entity.getId())
                .memberAccountId(entity.getMemberAccountId())
                .thumbnailImageId(entity.getThumbnailImageId())
                .title(entity.getTitle())
                .previewSummary(entity.getPreviewSummary())
                .privateMemo(entity.getPrivateMemo())
                .cachedViewCount(entity.getCachedViewCount())
                .cachedInterestCount(entity.getCachedInterestCount())
                .resumeJobCategory(resumeJobCategoryMapper.toDomain(entity.getResumeJobCategory()))
                .resumeContent(resumeContentMapper.toDomain(entity.getResumeContent()))
                .externalLinks(entity.getExternalLinks())
                .resumeTags(toDomainTags(entity.getResumeTags()))
                .portfolioIds(entity.getPortfolioIds() != null ? entity.getPortfolioIds() : List.of())
                .status(entity.getStatus())
                .collaborationType(entity.getCollaborationType())
                .visibility(entity.getVisibility())
                .auditingInfo(toAuditingInfo(entity.getAuditingInfo()))
                .build();
    }

    private List<ResumeTag> toDomainTags(List<ResumeTagJpaEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }
        return entities.stream()
                .map(resumeTagMapper::toDomain)
                .toList();
    }

    private AuditingInfo toAuditingInfo(AuditingJpaInfo info) {
        if (info == null) {
            return null;
        }
        return new AuditingInfo(info.createdAt(), info.updatedAt());
    }
}

package io.hirecore.hirecorememberserver.modules.resume.adapter.out.persistence.jpa.mapper;

import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.resume.adapter.out.persistence.jpa.entity.ResumeContentJpaEntity;
import io.hirecore.hirecorememberserver.modules.resume.domain.ResumeContent;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.AuditingJpaInfo;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.AuditingInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = GlobalMapStructConfig.class)
public abstract class ResumeContentJpaEntityMapper {

    @Mapping(target = "id", source = "resumeId")
    @Mapping(target = "resume", ignore = true)
    public abstract ResumeContentJpaEntity toJpaEntity(ResumeContent domain);

    public ResumeContent toDomain(ResumeContentJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return ResumeContent.builder()
                .resumeId(entity.getId())
                .contentJson(entity.getContentJson())
                .contentHtml(entity.getContentHtml())
                .imageIds(entity.getImageIds() != null ? entity.getImageIds() : List.of())
                .auditingInfo(toAuditingInfo(entity.getAuditingInfo()))
                .build();
    }

    private AuditingInfo toAuditingInfo(AuditingJpaInfo info) {
        if (info == null) {
            return null;
        }
        return new AuditingInfo(info.createdAt(), info.updatedAt());
    }
}

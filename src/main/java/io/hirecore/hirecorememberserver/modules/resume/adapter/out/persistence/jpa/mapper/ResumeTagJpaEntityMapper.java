package io.hirecore.hirecorememberserver.modules.resume.adapter.out.persistence.jpa.mapper;

import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.resume.adapter.out.persistence.jpa.entity.ResumeTagJpaEntity;
import io.hirecore.hirecorememberserver.modules.resume.domain.ResumeTag;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.AuditingJpaInfo;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.AuditingInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = GlobalMapStructConfig.class)
public abstract class ResumeTagJpaEntityMapper {

    @Mapping(target = "resume", ignore = true)
    public abstract ResumeTagJpaEntity toJpaEntity(ResumeTag domain);

    public ResumeTag toDomain(ResumeTagJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return ResumeTag.builder()
                .id(entity.getId())
                .name(entity.getName())
                .normalizedTag(entity.getNormalizedTag())
                .sortOrder(entity.getSortOrder())
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

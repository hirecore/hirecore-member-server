package io.hirecore.hirecorememberserver.modules.coverletter.adapter.out.persistence.jpa.mapper;

import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.coverletter.adapter.out.persistence.jpa.entity.CoverLetterTagJpaEntity;
import io.hirecore.hirecorememberserver.modules.coverletter.domain.CoverLetterTag;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.AuditingJpaInfo;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.AuditingInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = GlobalMapStructConfig.class)
public abstract class CoverLetterTagJpaEntityMapper {

    @Mapping(target = "coverLetter", ignore = true)
    public abstract CoverLetterTagJpaEntity toJpaEntity(CoverLetterTag domain);

    public CoverLetterTag toDomain(CoverLetterTagJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return CoverLetterTag.builder()
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

package io.hirecore.hirecorememberserver.modules.coverletter.adapter.out.persistence.jpa.mapper;

import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.coverletter.adapter.out.persistence.jpa.entity.CoverLetterContentJpaEntity;
import io.hirecore.hirecorememberserver.modules.coverletter.domain.CoverLetterContent;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.AuditingJpaInfo;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.AuditingInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = GlobalMapStructConfig.class)
public abstract class CoverLetterContentJpaEntityMapper {

    @Mapping(target = "id", source = "coverLetterId")
    @Mapping(target = "coverLetter", ignore = true)
    public abstract CoverLetterContentJpaEntity toJpaEntity(CoverLetterContent domain);

    public CoverLetterContent toDomain(CoverLetterContentJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return CoverLetterContent.builder()
                .coverLetterId(entity.getId())
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

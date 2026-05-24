package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper;

import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioTagJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioTag;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.AuditingJpaInfo;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.AuditingInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = GlobalMapStructConfig.class)
public abstract class PortfolioTagJpaEntityMapper {

    @Mapping(target = "portfolio", ignore = true)
    public abstract PortfolioTagJpaEntity toJpaEntity(PortfolioTag domain);

    public PortfolioTag toDomain(PortfolioTagJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return PortfolioTag.builder()
                .id(entity.getId())
                .userInputTag(entity.getUserInputTag())
                .normalizedTag(entity.getNormalizedTag())
                .sortOrder(entity.getSortOrder())
                .isDeleted(entity.getIsDeleted())
                .deletedAt(entity.getDeletedAt())
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

package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper;

import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioContentJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioContent;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.AuditingJpaInfo;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.AuditingInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = GlobalMapStructConfig.class)
public abstract class PortfolioContentJpaEntityMapper {

    @Mapping(target = "id", source = "portfolioId")
    @Mapping(target = "portfolio", ignore = true)
    public abstract PortfolioContentJpaEntity toJpaEntity(PortfolioContent domain);

    public PortfolioContent toDomain(PortfolioContentJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return PortfolioContent.builder()
                .portfolioId(entity.getId())
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

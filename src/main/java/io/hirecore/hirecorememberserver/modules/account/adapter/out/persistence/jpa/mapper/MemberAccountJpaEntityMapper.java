package io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.mapper;


import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.entity.MemberAccountJpaEntity;
import io.hirecore.hirecorememberserver.modules.account.domain.MemberAccount;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapStructConfig.class
)
public abstract class MemberAccountJpaEntityMapper {
    public abstract MemberAccountJpaEntity toJpaEntity(MemberAccount domain);
    public abstract MemberAccount toDomain(MemberAccountJpaEntity jpaEntity);
}

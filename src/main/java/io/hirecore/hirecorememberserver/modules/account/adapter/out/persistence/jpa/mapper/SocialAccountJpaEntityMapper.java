package io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.mapper;


import io.hirecore.hirecorememberserver.common.mapper.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.entity.SocialAccountJpaEntity;
import io.hirecore.hirecorememberserver.modules.account.domain.SocialAccount;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapStructConfig.class
)
public abstract class SocialAccountJpaEntityMapper {
    public abstract SocialAccountJpaEntity toJpaEntity(SocialAccount domain);
    public abstract SocialAccount toDomain(SocialAccountJpaEntity jpaEntity);
}

package io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.mapper;

import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.entity.UserProfileDetailJpaEntity;
import io.hirecore.hirecorememberserver.modules.profile.domain.UserProfileDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        config = GlobalMapStructConfig.class
)
public abstract class UserProfileDetailJpaEntityMapper {
    public abstract UserProfileDetailJpaEntity toJpaEntity(UserProfileDetail domain);

    @Mapping(source = "id", target = "profileId")
    public abstract UserProfileDetail toDomain(UserProfileDetailJpaEntity jpaEntity);
}

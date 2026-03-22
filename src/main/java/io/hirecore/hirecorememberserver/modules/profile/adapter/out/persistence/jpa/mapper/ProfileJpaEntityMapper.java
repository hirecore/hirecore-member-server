package io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.mapper;

import io.hirecore.hirecorememberserver.common.mapper.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.entity.ProfileDetailJpaEntity;
import io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.entity.ProfileJpaEntity;
import io.hirecore.hirecorememberserver.modules.profile.domain.Profile;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(
        config = GlobalMapStructConfig.class,
        uses = {ProfileDetailJpaEntityMapper.class}
)
public abstract class ProfileJpaEntityMapper {

    public abstract ProfileJpaEntity toJpaEntity(Profile domain);
    public abstract Profile toDomain(ProfileJpaEntity jpaEntity);

    @AfterMapping
    protected void linkProfileDetail(@MappingTarget ProfileJpaEntity profileJpaEntity) {
        ProfileDetailJpaEntity profileDetail = profileJpaEntity.getProfileDetail();
        if (profileDetail != null) {
            profileJpaEntity.syncProfileDetail(profileDetail);
        }
    }
}

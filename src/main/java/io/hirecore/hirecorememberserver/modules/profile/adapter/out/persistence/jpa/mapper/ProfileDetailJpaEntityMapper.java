package io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.mapper;

import io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.entity.ProfileDetailJpaEntity;
import io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.entity.UserProfileDetailJpaEntity;
import io.hirecore.hirecorememberserver.modules.profile.domain.ProfileDetail;
import io.hirecore.hirecorememberserver.modules.profile.domain.UserProfileDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// ProfileDetail ↔ JPA 엔티티 다형성 변환 (sealed ↔ JPA 상속, instanceof 분기)
@Component
@RequiredArgsConstructor
public class ProfileDetailJpaEntityMapper {

    private final UserProfileDetailJpaEntityMapper userProfileDetailJpaEntityMapper;

    public ProfileDetailJpaEntity toJpaEntity(ProfileDetail detail) {
        if (detail == null) {
            return null;
        }

        if (detail instanceof UserProfileDetail userProfileDetail) {
            return userProfileDetailJpaEntityMapper.toJpaEntity(userProfileDetail);
        }

        throw new IllegalArgumentException(
                "Unsupported ProfileDetail type: " + detail.getClass().getName()
        );
    }

    public ProfileDetail toDomain(ProfileDetailJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        if (jpaEntity instanceof UserProfileDetailJpaEntity userProfileDetailJpaEntity) {
            return userProfileDetailJpaEntityMapper.toDomain(userProfileDetailJpaEntity);
        }

        throw new IllegalArgumentException(
                "Unsupported ProfileDetailJpaEntity type: " + jpaEntity.getClass().getName()
        );
    }
}

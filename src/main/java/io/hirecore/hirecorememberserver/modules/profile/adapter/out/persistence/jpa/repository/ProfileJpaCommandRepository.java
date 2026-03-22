package io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.entity.ProfileJpaEntity;
import org.springframework.data.repository.Repository;

public interface ProfileJpaCommandRepository extends Repository<ProfileJpaEntity, Long> {
    ProfileJpaEntity save(ProfileJpaEntity entity);
}

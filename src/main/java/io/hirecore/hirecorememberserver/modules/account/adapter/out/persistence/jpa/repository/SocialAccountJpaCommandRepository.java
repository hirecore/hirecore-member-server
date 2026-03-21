package io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.entity.SocialAccountJpaEntity;
import org.springframework.data.repository.Repository;

public interface SocialAccountJpaCommandRepository extends Repository<SocialAccountJpaEntity, Long> {
    SocialAccountJpaEntity save(SocialAccountJpaEntity socialAccountJpaEntity);
}

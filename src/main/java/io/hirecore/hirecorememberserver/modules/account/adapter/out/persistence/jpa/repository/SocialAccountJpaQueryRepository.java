package io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.entity.SocialAccountJpaEntity;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.OAuth2Provider;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface SocialAccountJpaQueryRepository extends Repository<SocialAccountJpaEntity, Long> {
    Optional<SocialAccountJpaEntity> findByProviderAndProviderId(OAuth2Provider provider, String providerId);
}

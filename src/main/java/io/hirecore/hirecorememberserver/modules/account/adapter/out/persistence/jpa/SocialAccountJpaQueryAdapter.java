package io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.mapper.SocialAccountJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.repository.SocialAccountJpaQueryRepository;
import io.hirecore.hirecorememberserver.modules.account.domain.SocialAccount;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.OAuth2Provider;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.LoadSocialAccountPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SocialAccountJpaQueryAdapter implements LoadSocialAccountPort {

    private final SocialAccountJpaQueryRepository socialAccountJpaQueryRepository;
    private final SocialAccountJpaEntityMapper socialAccountJpaEntityMapper;

    @Override
    public Optional<SocialAccount> findByProviderAndProviderId(OAuth2Provider provider, String providerId) {
        return socialAccountJpaQueryRepository.findByProviderAndProviderId(provider, providerId)
                .map(socialAccountJpaEntityMapper::toDomain);
    }
}

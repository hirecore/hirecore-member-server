package io.hirecore.hirecorememberserver.modules.account.application.port.out;

import io.hirecore.hirecorememberserver.modules.account.domain.SocialAccount;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.OAuth2Provider;

import java.util.Optional;

public interface LoadSocialAccountPort {
    Optional<SocialAccount> findByProviderAndProviderId(OAuth2Provider provider, String providerId);
}

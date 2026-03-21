package io.hirecore.hirecorememberserver.modules.account.application.port.out;

import io.hirecore.hirecorememberserver.modules.account.domain.SocialAccount;
import io.hirecore.hirecorememberserver.sharedkernel.vo.OAuth2Provider;

import java.util.Optional;

public interface SocialAccountQueryPort {
    Optional<SocialAccount> findByProviderAndProviderId(OAuth2Provider provider, String providerId);
}

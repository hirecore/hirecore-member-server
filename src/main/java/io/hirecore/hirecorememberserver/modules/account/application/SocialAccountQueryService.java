package io.hirecore.hirecorememberserver.modules.account.application;

import io.hirecore.hirecorememberserver.modules.account.domain.SocialAccount;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.OAuth2Provider;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.LoadSocialAccountPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SocialAccountQueryService {
    private final LoadSocialAccountPort socialAccountQueryPort;

    // 기존 가입 계정 조회
    @Transactional(readOnly = true)
    public Optional<SocialAccount> findExistingSocialAccount(OAuth2Provider provider, String providerId) {
        return socialAccountQueryPort.findByProviderAndProviderId(provider, providerId);
    }
}

package io.hirecore.hirecorememberserver.modules.account.application;

import com.navercorp.fixturemonkey.FixtureMonkey;
import io.hirecore.hirecorememberserver.modules.account.domain.SocialAccount;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.LoadSocialAccountPort;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.OAuth2Provider;
import io.hirecore.hirecorememberserver.support.FixtureMonkeyFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@DisplayName("QuerySocialAccountService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class SocialAccountQueryServiceTest {

    private final FixtureMonkey fm = FixtureMonkeyFactory.monkey();

    @InjectMocks
    private SocialAccountQueryService service;

    @Mock
    private LoadSocialAccountPort socialAccountQueryPort;

    @Test
    @DisplayName("유효한 provider와 providerId로 기존 소셜 계정을 조회한다")
    void should_find_existing_social_account() {
        // given
        SocialAccount existingAccount = fm.giveMeOne(SocialAccount.class);
        OAuth2Provider provider = existingAccount.getProvider();
        String providerId = existingAccount.getProviderId();

        given(socialAccountQueryPort.findByProviderAndProviderId(provider, providerId))
                .willReturn(Optional.of(existingAccount));

        // when
        Optional<SocialAccount> result = service.findExistingSocialAccount(provider, providerId);

        // then
        assertThat(result).hasValueSatisfying(a -> assertThat(a).isSameAs(existingAccount));
    }

    @Test
    @DisplayName("존재하지 않는 소셜 계정이면 빈 Optional을 반환한다")
    void should_return_empty_when_not_found() {
        // given
        given(socialAccountQueryPort.findByProviderAndProviderId(OAuth2Provider.KAKAO, "non-existent"))
                .willReturn(Optional.empty());

        // when
        Optional<SocialAccount> result = service.findExistingSocialAccount(OAuth2Provider.KAKAO, "non-existent");

        // then
        assertThat(result).isEmpty();
    }
}

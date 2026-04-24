package io.hirecore.hirecorememberserver.modules.account.application;

import com.navercorp.fixturemonkey.FixtureMonkey;
import io.hirecore.hirecorememberserver.modules.account.domain.SocialAccount;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.SaveSocialAccountPort;
import io.hirecore.hirecorememberserver.support.FixtureMonkeyFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("CommandSocialAccountService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class SocialAccountCommandServiceTest {

    private final FixtureMonkey fm = FixtureMonkeyFactory.monkey();

    @InjectMocks
    private SocialAccountCommandService service;

    @Mock
    private SaveSocialAccountPort socialAccountCommandPort;

    @Nested
    @DisplayName("소셜 계정 저장")
    class SaveSocialAccountTest {

        @Test
        @DisplayName("소셜 계정 도메인 객체를 받아 포트를 통해 저장한다")
        void should_save_social_account_via_port() {
            // given
            SocialAccount socialAccount = fm.giveMeOne(SocialAccount.class);
            SocialAccount savedAccount = fm.giveMeOne(SocialAccount.class);
            given(socialAccountCommandPort.save(socialAccount)).willReturn(savedAccount);

            // when
            SocialAccount result = service.saveSocialAccount(socialAccount);

            // then
            assertThat(result).isSameAs(savedAccount);
            then(socialAccountCommandPort).should().save(socialAccount);
        }

        @Test
        @DisplayName("포트에서 예외 발생 시 그대로 전파된다")
        void should_propagate_exception_from_port() {
            // given
            SocialAccount socialAccount = fm.giveMeOne(SocialAccount.class);
            given(socialAccountCommandPort.save(socialAccount))
                    .willThrow(new RuntimeException("DB 저장 실패"));

            // when & then
            assertThatThrownBy(() -> service.saveSocialAccount(socialAccount))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("DB 저장 실패");
        }
    }
}

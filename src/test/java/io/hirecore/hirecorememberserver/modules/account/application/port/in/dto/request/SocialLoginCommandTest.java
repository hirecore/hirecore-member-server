package io.hirecore.hirecorememberserver.modules.account.application.port.in.dto.request;

import com.navercorp.fixturemonkey.FixtureMonkey;
import io.hirecore.hirecorememberserver.modules.account.application.exception.SocialAccountApplicationException;
import io.hirecore.hirecorememberserver.modules.account.application.port.in.LoginSocialUserUseCase;
import io.hirecore.hirecorememberserver.modules.account.application.exception.SocialAccountApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.support.FixtureMonkeyFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("LoginSocialUserUseCase.Command 생성 단위 테스트")
class SocialLoginCommandTest {

    private final FixtureMonkey fm = FixtureMonkeyFactory.monkey();

    @Nested
    @DisplayName("정상 생성")
    class CreationTest {

        @Test
        @DisplayName("유효한 provider와 authorizationCode로 Command를 생성한다")
        void should_create_command_with_valid_values() {
            // when
            LoginSocialUserUseCase.Command command = fm.giveMeOne(LoginSocialUserUseCase.Command.class);

            // then
            assertThat(command.provider()).isNotBlank();
            assertThat(command.authorizationCode()).isNotBlank();
        }
    }

    @Nested
    @DisplayName("불변식 (Invariants) 검증")
    class InvariantsTest {

        @Test
        @DisplayName("provider가 null이면 PROVIDER_MISSING 에러코드로 예외가 발생한다")
        void should_throw_when_provider_is_null() {
            assertThatThrownBy(() -> new LoginSocialUserUseCase.Command(null, "code"))
                    .isInstanceOf(SocialAccountApplicationException.class)
                    .extracting("errorCode")
                    .isEqualTo(SocialAccountApplicationExceptionCodeCluster.DetailResponse.PROVIDER_MISSING.getErrorCode());
        }

        @Test
        @DisplayName("provider가 공백이면 PROVIDER_MISSING 에러코드로 예외가 발생한다")
        void should_throw_when_provider_is_blank() {
            assertThatThrownBy(() -> new LoginSocialUserUseCase.Command("  ", "code"))
                    .isInstanceOf(SocialAccountApplicationException.class)
                    .extracting("errorCode")
                    .isEqualTo(SocialAccountApplicationExceptionCodeCluster.DetailResponse.PROVIDER_MISSING.getErrorCode());
        }

        @Test
        @DisplayName("authorizationCode가 null이면 AUTHORIZATION_CODE_MISSING 에러코드로 예외가 발생한다")
        void should_throw_when_authorizationCode_is_null() {
            assertThatThrownBy(() -> new LoginSocialUserUseCase.Command("KAKAO", null))
                    .isInstanceOf(SocialAccountApplicationException.class)
                    .extracting("errorCode")
                    .isEqualTo(SocialAccountApplicationExceptionCodeCluster.DetailResponse.AUTHORIZATION_CODE_MISSING.getErrorCode());
        }

        @Test
        @DisplayName("authorizationCode가 공백이면 AUTHORIZATION_CODE_MISSING 에러코드로 예외가 발생한다")
        void should_throw_when_authorizationCode_is_blank() {
            assertThatThrownBy(() -> new LoginSocialUserUseCase.Command("KAKAO", "  "))
                    .isInstanceOf(SocialAccountApplicationException.class)
                    .extracting("errorCode")
                    .isEqualTo(SocialAccountApplicationExceptionCodeCluster.DetailResponse.AUTHORIZATION_CODE_MISSING.getErrorCode());
        }
    }
}

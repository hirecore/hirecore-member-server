package io.hirecore.hirecorememberserver.modules.account.application.port.out.dto.response;

import com.navercorp.fixturemonkey.FixtureMonkey;
import io.hirecore.hirecorememberserver.modules.account.application.exception.SocialAccountApplicationException;
import io.hirecore.hirecorememberserver.modules.account.application.exception.SocialAccountApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.OAuth2Provider;
import io.hirecore.hirecorememberserver.support.FixtureMonkeyFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("SocialUserProfile 단위 테스트")
class SocialUserProfileDetailResultTest {

    private final FixtureMonkey fm = FixtureMonkeyFactory.monkey();

    private static final OAuth2Provider VALID_PROVIDER = OAuth2Provider.KAKAO;
    private static final String VALID_PROVIDER_ID = "123456789";
    private static final String VALID_EMAIL = "user@kakao.com";
    private static final String VALID_NICKNAME = "테스트유저";
    private static final Instant VALID_CONNECTED_AT = Instant.parse("2025-01-01T00:00:00Z");

    @Nested
    @DisplayName("정상 생성")
    class CreationTest {

        @Test
        @DisplayName("모든 유효한 값으로 SocialUserProfile을 생성한다")
        void should_create_profile_with_all_valid_values() {
            // when
            SocialUserProfileResult profile = fm.giveMeOne(SocialUserProfileResult.class);

            // then
            assertThat(profile).satisfies(p -> {
                assertThat(p.provider()).isNotNull();
                assertThat(p.providerId()).isNotNull();
                assertThat(p.email()).isNotBlank();
                assertThat(p.nickname()).isNotBlank();
                assertThat(p.connectedAt()).isNotNull();
                assertThat(p.emailAgreed()).isNotNull();
                assertThat(p.profileNicknameAgreed()).isNotNull();
            });
        }

        @Test
        @DisplayName("동의항목이 false여도 정상 생성된다")
        void should_create_profile_with_false_agreements() {
            // when
            SocialUserProfileResult profile = fm.giveMeBuilder(SocialUserProfileResult.class)
                    .set("emailAgreed", false)
                    .set("profileNicknameAgreed", false)
                    .sample();

            // then
            assertThat(profile.emailAgreed()).isFalse();
            assertThat(profile.profileNicknameAgreed()).isFalse();
        }
    }

    @Nested
    @DisplayName("불변식 (Invariants) 검증")
    class InvariantsTest {

        @Test
        @DisplayName("provider가 null이면 PROVIDER_MISSING 에러코드로 예외가 발생한다")
        void should_throw_when_provider_is_null() {
            assertThatThrownBy(() -> new SocialUserProfileResult(
                    null, VALID_PROVIDER_ID, VALID_EMAIL, VALID_NICKNAME, VALID_CONNECTED_AT, true, true))
                    .isInstanceOf(SocialAccountApplicationException.class)
                    .extracting("errorCode")
                    .isEqualTo(SocialAccountApplicationExceptionCodeCluster.DetailResponse.USER_PROFILE_PROVIDER_MISSING.getErrorCode());
        }

        @Test
        @DisplayName("providerId가 null이면 PROVIDER_ID_MISSING 에러코드로 예외가 발생한다")
        void should_throw_when_providerId_is_null() {
            assertThatThrownBy(() -> new SocialUserProfileResult(
                    VALID_PROVIDER, null, VALID_EMAIL, VALID_NICKNAME, VALID_CONNECTED_AT, true, true))
                    .isInstanceOf(SocialAccountApplicationException.class)
                    .extracting("errorCode")
                    .isEqualTo(SocialAccountApplicationExceptionCodeCluster.DetailResponse.USER_PROFILE_PROVIDER_ID_MISSING.getErrorCode());
        }

        @ParameterizedTest(name = "email이 \"{0}\"이면 EMAIL_MISSING 에러가 발생한다")
        @NullAndEmptySource
        @ValueSource(strings = {"  "})
        @DisplayName("email이 null이거나 빈 값이면 EMAIL_MISSING 에러코드로 예외가 발생한다")
        void should_throw_when_email_is_blank_or_null(String email) {
            assertThatThrownBy(() -> new SocialUserProfileResult(
                    VALID_PROVIDER, VALID_PROVIDER_ID, email,
                    VALID_NICKNAME, VALID_CONNECTED_AT, true, true))
                    .isInstanceOf(SocialAccountApplicationException.class)
                    .extracting("errorCode")
                    .isEqualTo(SocialAccountApplicationExceptionCodeCluster.DetailResponse.USER_PROFILE_EMAIL_MISSING.getErrorCode());
        }

        @ParameterizedTest(name = "nickname이 \"{0}\"이면 NICKNAME_MISSING 에러가 발생한다")
        @NullAndEmptySource
        @ValueSource(strings = {"  "})
        @DisplayName("nickname이 null이거나 빈 값이면 NICKNAME_MISSING 에러코드로 예외가 발생한다")
        void should_throw_when_nickname_is_blank_or_null(String nickname) {
            assertThatThrownBy(() -> new SocialUserProfileResult(
                    VALID_PROVIDER, VALID_PROVIDER_ID, VALID_EMAIL,
                    nickname, VALID_CONNECTED_AT, true, true))
                    .isInstanceOf(SocialAccountApplicationException.class)
                    .extracting("errorCode")
                    .isEqualTo(SocialAccountApplicationExceptionCodeCluster.DetailResponse.USER_PROFILE_NICKNAME_MISSING.getErrorCode());
        }

        @Test
        @DisplayName("connectedAt이 null이면 CONNECTED_AT_MISSING 에러코드로 예외가 발생한다")
        void should_throw_when_connectedAt_is_null() {
            assertThatThrownBy(() -> new SocialUserProfileResult(
                    VALID_PROVIDER, VALID_PROVIDER_ID, VALID_EMAIL, VALID_NICKNAME, null, true, true))
                    .isInstanceOf(SocialAccountApplicationException.class)
                    .extracting("errorCode")
                    .isEqualTo(SocialAccountApplicationExceptionCodeCluster.DetailResponse.USER_PROFILE_CONNECTED_AT_MISSING.getErrorCode());
        }

        @Test
        @DisplayName("emailAgreed가 null이면 CONSENT_INFO_MISSING 에러코드로 예외가 발생한다")
        void should_throw_when_emailAgreed_is_null() {
            assertThatThrownBy(() -> new SocialUserProfileResult(
                    VALID_PROVIDER, VALID_PROVIDER_ID, VALID_EMAIL, VALID_NICKNAME, VALID_CONNECTED_AT, null, true))
                    .isInstanceOf(SocialAccountApplicationException.class)
                    .extracting("errorCode")
                    .isEqualTo(SocialAccountApplicationExceptionCodeCluster.DetailResponse.USER_PROFILE_CONSENT_INFO_MISSING.getErrorCode());
        }

        @Test
        @DisplayName("profileNicknameAgreed가 null이면 CONSENT_INFO_MISSING 에러코드로 예외가 발생한다")
        void should_throw_when_profileNicknameAgreed_is_null() {
            assertThatThrownBy(() -> new SocialUserProfileResult(
                    VALID_PROVIDER, VALID_PROVIDER_ID, VALID_EMAIL, VALID_NICKNAME, VALID_CONNECTED_AT, true, null))
                    .isInstanceOf(SocialAccountApplicationException.class)
                    .extracting("errorCode")
                    .isEqualTo(SocialAccountApplicationExceptionCodeCluster.DetailResponse.USER_PROFILE_CONSENT_INFO_MISSING.getErrorCode());
        }
    }
}

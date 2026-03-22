package io.hirecore.hirecorememberserver.modules.account.domain.vo;

import io.hirecore.hirecorememberserver.modules.account.domain.exception.SocialAccountDomainException;
import io.hirecore.hirecorememberserver.modules.account.domain.exception.SocialAccountDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.vo.OAuth2Provider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("SocialUserProfileInfo VO 단위 테스트")
class SocialUserProfileDetailInfoTest {

    @Nested
    @DisplayName("불변식 (Invariants) 검증")
    class InvariantsTest {

        @Test
        @DisplayName("provider가 null이면 PROVIDER_MISSING 에러코드로 예외가 발생한다")
        void should_throw_when_provider_is_null() {
            assertThatThrownBy(() -> new SocialUserProfileInfo(
                    null, "providerId", "email@test.com", Instant.now(), true, true))
                    .isInstanceOf(SocialAccountDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(SocialAccountDomainExceptionCodeCluster.HiddenDetailResponse.PROVIDER_MISSING.getErrorCode());
        }

        @Test
        @DisplayName("providerId가 null이면 PROVIDER_ID_MISSING 에러코드로 예외가 발생한다")
        void should_throw_when_providerId_is_null() {
            assertThatThrownBy(() -> new SocialUserProfileInfo(
                    OAuth2Provider.KAKAO, null, "email@test.com", Instant.now(), true, true))
                    .isInstanceOf(SocialAccountDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(SocialAccountDomainExceptionCodeCluster.HiddenDetailResponse.PROVIDER_ID_MISSING.getErrorCode());
        }

        @Test
        @DisplayName("connectedAt이 null이면 CONNECTED_AT_MISSING 에러코드로 예외가 발생한다")
        void should_throw_when_connectedAt_is_null() {
            assertThatThrownBy(() -> new SocialUserProfileInfo(
                    OAuth2Provider.KAKAO, "providerId", "email@test.com", null, true, true))
                    .isInstanceOf(SocialAccountDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(SocialAccountDomainExceptionCodeCluster.HiddenDetailResponse.CONNECTED_AT_MISSING.getErrorCode());
        }

        @Test
        @DisplayName("emailAgreed가 null이면 CONSENT_INFO_MISSING 에러코드로 예외가 발생한다")
        void should_throw_when_emailAgreed_is_null() {
            assertThatThrownBy(() -> new SocialUserProfileInfo(
                    OAuth2Provider.KAKAO, "providerId", "email@test.com", Instant.now(), null, true))
                    .isInstanceOf(SocialAccountDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(SocialAccountDomainExceptionCodeCluster.HiddenDetailResponse.CONSENT_INFO_MISSING.getErrorCode());
        }

        @Test
        @DisplayName("profileNicknameAgreed가 null이면 CONSENT_INFO_MISSING 에러코드로 예외가 발생한다")
        void should_throw_when_profileNicknameAgreed_is_null() {
            assertThatThrownBy(() -> new SocialUserProfileInfo(
                    OAuth2Provider.KAKAO, "providerId", "email@test.com", Instant.now(), true, null))
                    .isInstanceOf(SocialAccountDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(SocialAccountDomainExceptionCodeCluster.HiddenDetailResponse.CONSENT_INFO_MISSING.getErrorCode());
        }
    }
}

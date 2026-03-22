package io.hirecore.hirecorememberserver.modules.account.domain;

import io.hirecore.hirecorememberserver.modules.account.domain.exception.SocialAccountDomainException;
import io.hirecore.hirecorememberserver.modules.account.domain.exception.SocialAccountDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.vo.OAuth2Provider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("SocialAccount 도메인 단위 테스트")
class SocialAccountTest {

    private static final Long VALID_MEMBER_ID = 1L;
    private static final OAuth2Provider VALID_PROVIDER = OAuth2Provider.KAKAO;
    private static final String VALID_PROVIDER_ID = "kakao-123456";
    private static final String VALID_EMAIL = "user@kakao.com";

    @Nested
    @DisplayName("create() 팩토리 메서드")
    class CreateTest {

        @Test
        @DisplayName("유효한 값으로 SocialAccount를 생성한다")
        void should_create_social_account_with_valid_values() {
            // given
            Instant connectedAt = Instant.now();

            // when
            SocialAccount account = SocialAccount.create(
                    VALID_MEMBER_ID, VALID_PROVIDER, VALID_PROVIDER_ID, VALID_EMAIL, connectedAt, true, true);

            // then
            assertThat(account).satisfies(a -> {
                assertThat(a.getId()).isNotNull();
                assertThat(a.getMemberAccountId()).isEqualTo(VALID_MEMBER_ID);
                assertThat(a.getProvider()).isEqualTo(VALID_PROVIDER);
                assertThat(a.getProviderId()).isEqualTo(VALID_PROVIDER_ID);
                assertThat(a.getEmail()).isEqualTo(VALID_EMAIL);
                assertThat(a.getConnectedAt()).isEqualTo(connectedAt);
                assertThat(a.getEmailAgreed()).isTrue();
                assertThat(a.getProfileNicknameAgreed()).isTrue();
                assertThat(a.getAuditingInfo()).isNotNull();
            });
        }

        @Test
        @DisplayName("동의항목이 false인 경우에도 정상 생성된다")
        void should_create_social_account_with_false_agreements() {
            // when
            SocialAccount account = SocialAccount.create(
                    VALID_MEMBER_ID, VALID_PROVIDER, VALID_PROVIDER_ID, VALID_EMAIL, Instant.now(), false, false);

            // then
            assertThat(account.getEmailAgreed()).isFalse();
            assertThat(account.getProfileNicknameAgreed()).isFalse();
        }
    }

    @Nested
    @DisplayName("불변식 (Invariants) 검증")
    class InvariantsTest {

        @Test
        @DisplayName("provider가 null이면 PROVIDER_MISSING 에러코드로 예외가 발생한다")
        void should_throw_exception_when_provider_is_null() {
            assertThatThrownBy(() -> SocialAccount.create(1L, null, "pid", "email", Instant.now(), true, true))
                    .isInstanceOf(SocialAccountDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(SocialAccountDomainExceptionCodeCluster.HiddenDetailResponse.PROVIDER_MISSING.getErrorCode());
        }

        @Test
        @DisplayName("memberAccountId가 null이면 MEMBER_ACCOUNT_ID_MISSING 에러코드로 예외가 발생한다")
        void should_throw_exception_when_member_account_id_is_null() {
            assertThatThrownBy(() -> SocialAccount.create(null, OAuth2Provider.KAKAO, "pid", "email", Instant.now(), true, true))
                    .isInstanceOf(SocialAccountDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(SocialAccountDomainExceptionCodeCluster.HiddenDetailResponse.MEMBER_ACCOUNT_ID_MISSING.getErrorCode());
        }

        @Test
        @DisplayName("providerId가 null이면 PROVIDER_ID_MISSING 에러코드로 예외가 발생한다")
        void should_throw_exception_when_provider_id_is_null() {
            assertThatThrownBy(() -> SocialAccount.create(1L, OAuth2Provider.KAKAO, null, "email", Instant.now(), true, true))
                    .isInstanceOf(SocialAccountDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(SocialAccountDomainExceptionCodeCluster.HiddenDetailResponse.PROVIDER_ID_MISSING.getErrorCode());
        }

        @Test
        @DisplayName("connectedAt이 null이면 CONNECTED_AT_MISSING 에러코드로 예외가 발생한다")
        void should_throw_exception_when_connected_at_is_null() {
            assertThatThrownBy(() -> SocialAccount.create(1L, OAuth2Provider.KAKAO, "pid", "email", null, true, true))
                    .isInstanceOf(SocialAccountDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(SocialAccountDomainExceptionCodeCluster.HiddenDetailResponse.CONNECTED_AT_MISSING.getErrorCode());
        }

        @Test
        @DisplayName("emailAgreed가 null이면 CONSENT_INFO_MISSING 에러코드로 예외가 발생한다")
        void should_throw_exception_when_email_agreed_is_null() {
            assertThatThrownBy(() -> SocialAccount.create(1L, OAuth2Provider.KAKAO, "pid", "email", Instant.now(), null, true))
                    .isInstanceOf(SocialAccountDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(SocialAccountDomainExceptionCodeCluster.HiddenDetailResponse.CONSENT_INFO_MISSING.getErrorCode());
        }

        @Test
        @DisplayName("profileNicknameAgreed가 null이면 CONSENT_INFO_MISSING 에러코드로 예외가 발생한다")
        void should_throw_exception_when_profile_nickname_agreed_is_null() {
            assertThatThrownBy(() -> SocialAccount.create(1L, OAuth2Provider.KAKAO, "pid", "email", Instant.now(), true, null))
                    .isInstanceOf(SocialAccountDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(SocialAccountDomainExceptionCodeCluster.HiddenDetailResponse.CONSENT_INFO_MISSING.getErrorCode());
        }
    }
}

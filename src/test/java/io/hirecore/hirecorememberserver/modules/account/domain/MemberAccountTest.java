package io.hirecore.hirecorememberserver.modules.account.domain;

import io.hirecore.hirecorememberserver.modules.account.domain.exception.MemberAccountDomainException;
import io.hirecore.hirecorememberserver.modules.account.domain.exception.MemberAccountDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.account.domain.vo.MemberRole;
import io.hirecore.hirecorememberserver.modules.account.domain.vo.SocialUserProfileInfo;
import io.hirecore.hirecorememberserver.sharedkernel.domain.event.MemberAccountProfileCreatedEvent;
import io.hirecore.hirecorememberserver.sharedkernel.domain.event.MemberAccountSocialAccountCreatedEvent;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.OAuth2Provider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("MemberAccount 도메인 단위 테스트")
class MemberAccountTest {

    private static SocialUserProfileInfo validSocialInfo() {
        return new SocialUserProfileInfo(
                OAuth2Provider.KAKAO, "pid", "user@example.com", Instant.now(), true, true);
    }

    @Nested
    @DisplayName("createWithSocial() 팩토리 메서드")
    class CreateTest {

        @Test
        @DisplayName("유효한 이메일과 권한으로 MemberAccount를 생성한다")
        void should_create_member_account_with_valid_email_and_role() {
            // given
            String email = "user@example.com";
            MemberRole role = MemberRole.USER;

            // when
            MemberAccount memberAccount = MemberAccount.createWithSocial(email, role, validSocialInfo());

            // then
            assertThat(memberAccount).satisfies(account -> {
                assertThat(account.getEmail()).isEqualTo(email);
                assertThat(account.getRole()).isEqualTo(MemberRole.USER);
                assertThat(account.getId()).isNotNull();
                assertThat(account.getPassword()).isNull();
                assertThat(account.getAuditingInfo().createdAt()).isNotNull();
                assertThat(account.getAuditingInfo().updatedAt()).isNotNull();
            });
        }

        @ParameterizedTest(name = "MemberRole.{0}으로 MemberAccount를 생성할 수 있다")
        @EnumSource(MemberRole.class)
        @DisplayName("모든 권한 유형으로 MemberAccount를 생성할 수 있다")
        void should_create_member_account_with_any_role(MemberRole role) {
            // when
            MemberAccount memberAccount = MemberAccount.createWithSocial("user@example.com", role, validSocialInfo());

            // then
            assertThat(memberAccount.getRole()).isEqualTo(role);
        }
    }

    @Nested
    @DisplayName("불변식 (Invariants) 검증")
    class InvariantsTest {

        @Test
        @DisplayName("이메일이 null이면 EMAIL_MISSING 에러코드로 예외가 발생한다")
        void should_throw_exception_when_email_is_null() {
            assertThatThrownBy(() -> MemberAccount.createWithSocial(null, MemberRole.USER, validSocialInfo()))
                    .isInstanceOf(MemberAccountDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(MemberAccountDomainExceptionCodeCluster.HiddenDetailResponse.EMAIL_MISSING.getErrorCode());
        }

        @Test
        @DisplayName("이메일이 빈 문자열이면 EMAIL_MISSING 에러코드로 예외가 발생한다")
        void should_throw_exception_when_email_is_blank() {
            assertThatThrownBy(() -> MemberAccount.createWithSocial(" ", MemberRole.USER, validSocialInfo()))
                    .isInstanceOf(MemberAccountDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(MemberAccountDomainExceptionCodeCluster.HiddenDetailResponse.EMAIL_MISSING.getErrorCode());
        }

        @Test
        @DisplayName("권한이 null이면 ROLE_MISSING 에러코드로 예외가 발생한다")
        void should_throw_exception_when_role_is_null() {
            assertThatThrownBy(() -> MemberAccount.createWithSocial("user@example.com", null, validSocialInfo()))
                    .isInstanceOf(MemberAccountDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(MemberAccountDomainExceptionCodeCluster.HiddenDetailResponse.ROLE_MISSING.getErrorCode());
        }
    }

    @Nested
    @DisplayName("linkSocialAccount()")
    class LinkSocialAccountTest {

        @Test
        @DisplayName("동의 항목이 모두 true이면 소셜 연동·프로필 생성 이벤트가 등록된다")
        void should_register_event_when_link_succeeds() {
            // given
            SocialUserProfileInfo info = new SocialUserProfileInfo(
                    OAuth2Provider.KAKAO, "pid", "user@example.com", Instant.now(), true, true);

            // when
            MemberAccount account = MemberAccount.createWithSocial("user@example.com", MemberRole.USER, info);

            // then — 소셜 계정 생성과 프로필 생성 이벤트가 각각 등록된다
            assertThat(account.pollAllEvents())
                    .hasSize(2)
                    .hasAtLeastOneElementOfType(MemberAccountSocialAccountCreatedEvent.class)
                    .hasAtLeastOneElementOfType(MemberAccountProfileCreatedEvent.class);
        }

        @Test
        @DisplayName("이메일 제공에 동의하지 않은(emailAgreed=false) 소셜 정보가 주어지면, SOCIAL_LINK_WITHOUT_EMAIL_AGREED 예외가 발생한다")
        void should_throw_when_emailAgreed_is_false() {
            // given
            String email = "email@test.com";
            SocialUserProfileInfo info = new SocialUserProfileInfo(
                    OAuth2Provider.KAKAO, "providerId", email, Instant.now(),
                    false, // 테스트 핵심: 이메일 동의 안 함
                    true
            );

            // when & then
            assertThatThrownBy(() -> MemberAccount.createWithSocial(email, MemberRole.USER, info))
                    .isInstanceOf(MemberAccountDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(MemberAccountDomainExceptionCodeCluster.HiddenDetailResponse.SOCIAL_LINK_WITHOUT_EMAIL_AGREED.getErrorCode());
        }

        @Test
        @DisplayName("프로필 닉네임 제공에 동의하지 않은(profileNicknameAgreed=false) 소셜 정보가 주어지면, SOCIAL_LINK_WITHOUT_PROFILE_NICKNAME_AGREED 예외가 발생한다")
        void should_throw_when_profileNicknameAgreed_is_false() {
            // given
            String email = "email@test.com";
            SocialUserProfileInfo info = new SocialUserProfileInfo(
                    OAuth2Provider.KAKAO, "providerId", email, Instant.now(),
                    true,
                    false // 테스트 핵심: 닉네임 동의 안 함
            );

            // when & then
            assertThatThrownBy(() -> MemberAccount.createWithSocial(email, MemberRole.USER, info))
                    .isInstanceOf(MemberAccountDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(MemberAccountDomainExceptionCodeCluster.HiddenDetailResponse.SOCIAL_LINK_WITHOUT_PROFILE_NICKNAME_AGREED.getErrorCode());
        }

        @Test
        @DisplayName("불변식 위반 시 객체 생성 자체가 실패하여 불완전한 이벤트가 시스템에 등록되는 것을 원천 차단한다")
        void should_not_register_event_when_invariant_fails() {
            // given
            String email = "email@test.com";
            SocialUserProfileInfo info = new SocialUserProfileInfo(
                    OAuth2Provider.KAKAO, "providerId", email, Instant.now(),
                    false, // 불변식 위반
                    true
            );

            // when & then
            assertThatThrownBy(() -> MemberAccount.createWithSocial(email, MemberRole.USER, info))
                    .isInstanceOf(MemberAccountDomainException.class);
        }
    }
}

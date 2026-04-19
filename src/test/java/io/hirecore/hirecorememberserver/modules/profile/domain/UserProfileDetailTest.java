package io.hirecore.hirecorememberserver.modules.profile.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserProfileDetail 도메인 단위 테스트")
class UserProfileDetailTest {

    @Nested
    @DisplayName("UserProfileDetail.builder().build() 생성")
    class CreateTest {

        @Test
        @DisplayName("유효한 마케팅 이메일로 UserProfileDetail을 생성하면 marketingEmail 값이 보존된다")
        void should_preserve_marketing_email_when_valid_email_given() {
            // given
            String email = "user@example.com";

            // when
            UserProfileDetail profile = UserProfileDetail.builder().profileId(1L).marketingEmail(email).build();

            // then
            assertThat(profile.getMarketingEmail()).isEqualTo(email);
        }

        @Test
        @DisplayName("빈 문자열(마케팅 이메일 미동의)로 UserProfileDetail을 생성할 수 있다")
        void should_create_user_profile_with_empty_marketing_email() {
            // when
            UserProfileDetail profile = UserProfileDetail.builder().profileId(1L).marketingEmail(null).build();

            // then
            assertThat(profile.getMarketingEmail()).isNull();
        }
    }

    @Nested
    @DisplayName("불변식 (Invariants) 검증")
    class InvariantsTest {
    }
}

package io.hirecore.hirecorememberserver.modules.profile.domain;

import io.hirecore.hirecorememberserver.modules.profile.domain.exception.ProfileDomainException;
import io.hirecore.hirecorememberserver.modules.profile.domain.exception.ProfileDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.profile.domain.vo.ProfileImageInfo;
import io.hirecore.hirecorememberserver.modules.profile.domain.vo.PublicCodeInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Profile 도메인 단위 테스트")
class ProfileTest {

    private static final Long VALID_MEMBER_ACCOUNT_ID = 1L;
    private static final PublicCodeInfo VALID_PUBLIC_CODE = new PublicCodeInfo("Ab3Xy9Zq");
    private static final String VALID_NICKNAME = "테스터";
    private static final String VALID_PHONE = "01012345678";
    private static final String VALID_MARKETING_EMAIL = null;

    @Nested
    @DisplayName("Profile.createUserProfile() 팩토리 메서드")
    class CreateTest {

        @Test
        @DisplayName("유효한 값으로 Profile을 생성하면 모든 필드가 올바르게 설정된다")
        void should_create_profile_with_all_fields_set_correctly() {
            // when
            Profile profile = Profile.createUserProfile(
                    VALID_MEMBER_ACCOUNT_ID, VALID_PUBLIC_CODE, VALID_NICKNAME, VALID_PHONE, VALID_MARKETING_EMAIL);

            // then
            assertThat(profile).satisfies(p -> {
                assertThat(p.getId()).isNotNull();
                assertThat(p.getMemberAccountId()).isEqualTo(VALID_MEMBER_ACCOUNT_ID);
                assertThat(p.getPublicCodeInfo()).isEqualTo(VALID_PUBLIC_CODE);
                assertThat(p.getNickname()).isEqualTo(VALID_NICKNAME);
                assertThat(p.getPhoneNumber()).isEqualTo(VALID_PHONE);
                assertThat(p.getProfileImageInfo()).isNotNull();
                assertThat(p.getAuditingInfo()).isNotNull();
                assertThat(p.getAuditingInfo().createdAt()).isNotNull();
                assertThat(p.getAuditingInfo().updatedAt()).isNotNull();
                assertThat(p.getProfileDetail()).isInstanceOf(UserProfileDetail.class);
            });
        }

        @Test
        @DisplayName("profileImageInfo는 기본값(모든 이미지 필드 null)으로 초기화된다")
        void should_initialize_profile_image_info_with_null_defaults() {
            // when
            Profile profile = Profile.createUserProfile(
                    VALID_MEMBER_ACCOUNT_ID, VALID_PUBLIC_CODE, VALID_NICKNAME, VALID_PHONE, VALID_MARKETING_EMAIL);

            // then
            ProfileImageInfo imageInfo = profile.getProfileImageInfo();
            assertThat(imageInfo.originProfileImageName()).isNull();
            assertThat(imageInfo.storageProfileImageName()).isNull();
            assertThat(imageInfo.storageProfileImagePath()).isNull();
        }

        @Test
        @DisplayName("phoneNumber가 null이어도 정상적으로 생성된다")
        void should_create_profile_without_phone_number() {
            // when
            Profile profile = Profile.createUserProfile(
                    VALID_MEMBER_ACCOUNT_ID, VALID_PUBLIC_CODE, VALID_NICKNAME, null, VALID_MARKETING_EMAIL);

            // then
            assertThat(profile.getPhoneNumber()).isNull();
            assertThat(profile.getId()).isNotNull();
        }

        @Test
        @DisplayName("각 호출마다 고유한 TSID가 생성된다")
        void should_generate_unique_id_for_each_instance() {
            // when
            Profile profile1 = Profile.createUserProfile(
                    VALID_MEMBER_ACCOUNT_ID, VALID_PUBLIC_CODE, VALID_NICKNAME, VALID_PHONE, VALID_MARKETING_EMAIL);
            Profile profile2 = Profile.createUserProfile(
                    VALID_MEMBER_ACCOUNT_ID, VALID_PUBLIC_CODE, VALID_NICKNAME, VALID_PHONE, VALID_MARKETING_EMAIL);

            // then
            assertThat(profile1.getId()).isNotEqualTo(profile2.getId());
        }
    }

    @Nested
    @DisplayName("불변식 (Invariants) 검증")
    class InvariantsTest {

        @Test
        @DisplayName("memberAccountId가 null이면 MEMBER_ACCOUNT_ID_MISSING 에러코드로 예외가 발생한다")
        void should_throw_when_member_account_id_is_null() {
            assertThatThrownBy(() -> Profile.createUserProfile(
                    null, VALID_PUBLIC_CODE, VALID_NICKNAME, VALID_PHONE, VALID_MARKETING_EMAIL))
                    .isInstanceOf(ProfileDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(ProfileDomainExceptionCodeCluster.HiddenDetailResponse.MEMBER_ACCOUNT_ID_MISSING.getErrorCode());
        }

        @Test
        @DisplayName("publicCodeInfo가 null이면 PUBLIC_CODE_INFO_NULL 에러코드로 예외가 발생한다")
        void should_throw_when_public_code_info_is_null() {
            assertThatThrownBy(() -> Profile.createUserProfile(
                    VALID_MEMBER_ACCOUNT_ID, null, VALID_NICKNAME, VALID_PHONE, VALID_MARKETING_EMAIL))
                    .isInstanceOf(ProfileDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(ProfileDomainExceptionCodeCluster.HiddenDetailResponse.PUBLIC_CODE_INFO_NULL.getErrorCode());
        }

        @Test
        @DisplayName("nickname이 null이면 NICKNAME_MISSING 에러코드로 예외가 발생한다")
        void should_throw_when_nickname_is_null() {
            assertThatThrownBy(() -> Profile.createUserProfile(
                    VALID_MEMBER_ACCOUNT_ID, VALID_PUBLIC_CODE, null, VALID_PHONE, VALID_MARKETING_EMAIL))
                    .isInstanceOf(ProfileDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(ProfileDomainExceptionCodeCluster.HiddenDetailResponse.NICKNAME_MISSING.getErrorCode());
        }

    }
}

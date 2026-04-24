package io.hirecore.hirecorememberserver.modules.profile.application.usecase;

import io.hirecore.hirecorememberserver.common.adapter.out.persistence.exception.DataConsistencyException;
import io.hirecore.hirecorememberserver.modules.profile.application.port.in.dto.response.UserProfileSummaryResponse;
import io.hirecore.hirecorememberserver.modules.profile.application.port.out.LoadUserProfileSummaryPort;
import io.hirecore.hirecorememberserver.modules.profile.application.port.out.dto.result.UserProfileSummaryResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

/**
 * {@link GetUserProfileSummaryUseCaseImpl} 단위 테스트.
 *
 * <p>JWT에서 추출한 회원 정보와 프로필 테이블 조회 결과를 조합하여
 * {@link UserProfileSummaryResponse}를 정확히 반환하는지 검증합니다.</p>
 */
@DisplayName("GetUserProfileSummaryUseCaseImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
class GetUserProfileSummaryUseCaseImplTest {

    private static final Long MEMBER_ID = 12345L;
    private static final String EMAIL = "user@hirecore.io";
    private static final String PUBLIC_CODE = "Ab3Xy9Zq";
    private static final String NICKNAME = "my-nickname";
    private static final String PROFILE_IMAGE_PATH = "/images/profile/abc123.png";

    @InjectMocks
    private GetUserProfileSummaryUseCaseImpl useCase;

    @Mock
    private LoadUserProfileSummaryPort userProfileQueryPort;

    @Nested
    @DisplayName("프로필이 존재하는 경우")
    class ProfileExistsTest {

        @Test
        @DisplayName("JWT 정보(id, email)와 프로필 정보(publicCode, nickname, profileImageUrl)를 조합하여 응답을 반환한다")
        void should_return_combined_response_with_jwt_and_profile_data() {
            // given
            UserProfileSummaryResult profile = new UserProfileSummaryResult(PUBLIC_CODE, NICKNAME, PROFILE_IMAGE_PATH);
            given(userProfileQueryPort.findUserProfileSummary(MEMBER_ID))
                    .willReturn(Optional.of(profile));

            // when
            UserProfileSummaryResponse response = useCase.execute(MEMBER_ID, EMAIL);

            // then
            assertThat(response).satisfies(r -> {
                assertThat(r.id()).isEqualTo(String.valueOf(MEMBER_ID));
                assertThat(r.email()).isEqualTo(EMAIL);
                assertThat(r.publicCode()).isEqualTo(PUBLIC_CODE);
                assertThat(r.nickname()).isEqualTo(NICKNAME);
                assertThat(r.profileImageUrl()).isEqualTo(PROFILE_IMAGE_PATH);
            });
        }

        @Test
        @DisplayName("프로필 이미지가 없으면 profileImageUrl이 null인 응답을 반환한다")
        void should_return_null_profile_image_url_when_image_not_set() {
            // given
            UserProfileSummaryResult profile = new UserProfileSummaryResult(PUBLIC_CODE, NICKNAME, null);
            given(userProfileQueryPort.findUserProfileSummary(MEMBER_ID))
                    .willReturn(Optional.of(profile));

            // when
            UserProfileSummaryResponse response = useCase.execute(MEMBER_ID, EMAIL);

            // then
            assertThat(response.profileImageUrl()).isNull();
            assertThat(response.nickname()).isEqualTo(NICKNAME);
        }
    }

    @Nested
    @DisplayName("프로필이 존재하지 않는 경우")
    class ProfileNotExistsTest {

        @Test
        @DisplayName("DataConsistencyException이 발생한다 (회원가입 시 프로필은 반드시 생성되므로 정합성 오류)")
        void should_throw_data_consistency_exception_when_profile_missing() {
            // given
            given(userProfileQueryPort.findUserProfileSummary(MEMBER_ID))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> useCase.execute(MEMBER_ID, EMAIL))
                    .isInstanceOf(DataConsistencyException.class)
                    .hasMessageContaining(String.valueOf(MEMBER_ID))
                    .hasMessageContaining("프로필");

            then(userProfileQueryPort).should().findUserProfileSummary(MEMBER_ID);
        }
    }
}

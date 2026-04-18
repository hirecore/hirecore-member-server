package io.hirecore.hirecorememberserver.modules.profile.adapter.in.event;

import io.hirecore.hirecorememberserver.modules.profile.application.ProfileCommandService;
import io.hirecore.hirecorememberserver.modules.profile.application.ProfileQueryService;
import io.hirecore.hirecorememberserver.modules.profile.domain.Profile;
import io.hirecore.hirecorememberserver.modules.profile.domain.UserProfileDetail;
import io.hirecore.hirecorememberserver.sharedkernel.event.MemberRegisteredEvent;
import io.hirecore.hirecorememberserver.sharedkernel.vo.OAuth2Provider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@DisplayName("MemberRegisteredProfileHandler 단위 테스트")
@ExtendWith(MockitoExtension.class)
class MemberRegisteredProfileHandlerTest {

    @InjectMocks
    private MemberRegisteredProfileHandler handler;

    @Mock
    private ProfileCommandService profileCommandService;

    @Mock
    private ProfileQueryService profileQueryService;

    @Captor
    private ArgumentCaptor<Profile> profileCaptor;

    @Nested
    @DisplayName("handleUserProfileCreation()")
    class HandleUserProfileDetailCreationTest {

        @Test
        @DisplayName("이벤트를 수신하면 이메일의 @ 앞부분을 닉네임으로 프로필을 생성하여 저장한다")
        void should_create_profile_with_nickname_derived_from_email() {
            // given
            MemberRegisteredEvent event = new MemberRegisteredEvent(
                    1L, OAuth2Provider.KAKAO, "kakao-id", "user@example.com", Instant.now(), true, true);
            given(profileQueryService.existsByPublicCode(anyString())).willReturn(false);

            // when
            handler.handleUserProfileCreation(event);

            // then — 이벤트 필드가 Profile에 정확히 매핑되었는지 검증
            then(profileCommandService).should().save(profileCaptor.capture());

            Profile captured = profileCaptor.getValue();
            assertThat(captured.getMemberAccountId()).isEqualTo(1L);
            assertThat(captured.getNickname()).isEqualTo("user");
            assertThat(captured.getPublicCodeInfo()).isNotNull();
            assertThat(captured.getPublicCodeInfo().publicCode()).hasSize(8);
            assertThat(captured.getProfileDetail()).isInstanceOf(UserProfileDetail.class);
            assertThat(((UserProfileDetail) captured.getProfileDetail()).getMarketingEmail()).isNull();
        }

        @Test
        @DisplayName("publicCode 충돌이 발생하면 고유한 코드가 생성될 때까지 재시도한다")
        void should_retry_public_code_generation_when_collision_occurs() {
            // given: 처음에는 충돌(true), 두 번째에는 성공(false)
            MemberRegisteredEvent event = new MemberRegisteredEvent(
                    2L, OAuth2Provider.KAKAO, "kakao-id", "test@kakao.com", Instant.now(), true, true);
            given(profileQueryService.existsByPublicCode(anyString()))
                    .willReturn(true)    // 첫 번째 생성 → 충돌
                    .willReturn(false);  // 두 번째 생성 → 성공

            // when
            handler.handleUserProfileCreation(event);

            // then: existsByPublicCode가 두 번 호출되어야 한다
            then(profileQueryService).should(times(2)).existsByPublicCode(anyString());
            then(profileCommandService).should().save(any(Profile.class));
        }
    }
}

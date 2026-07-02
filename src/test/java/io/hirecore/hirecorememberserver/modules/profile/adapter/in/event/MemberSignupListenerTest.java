package io.hirecore.hirecorememberserver.modules.profile.adapter.in.event;

import io.hirecore.hirecorememberserver.modules.profile.application.ProfileCommandService;
import io.hirecore.hirecorememberserver.modules.profile.application.ProfileQueryService;
import io.hirecore.hirecorememberserver.modules.profile.domain.Profile;
import io.hirecore.hirecorememberserver.modules.profile.domain.UserProfileDetail;
import io.hirecore.hirecorememberserver.sharedkernel.domain.event.MemberAccountProfileCreatedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@DisplayName("MemberSignupListener 단위 테스트")
@ExtendWith(MockitoExtension.class)
class MemberSignupListenerTest {

    @InjectMocks
    private MemberSignupListener listener;

    @Mock
    private ProfileCommandService profileCommandService;

    @Mock
    private ProfileQueryService profileQueryService;

    @Captor
    private ArgumentCaptor<Profile> profileCaptor;

    @Nested
    @DisplayName("handleMemberSignUp()")
    class HandleMemberSignUpTest {

        @Test
        @DisplayName("이벤트를 수신하면 이메일의 @ 앞부분을 닉네임으로 프로필을 생성하여 저장한다")
        void should_create_profile_with_nickname_derived_from_email() {
            // given
            MemberAccountProfileCreatedEvent event =
                    new MemberAccountProfileCreatedEvent(1L, "user@example.com");
            given(profileQueryService.existsByPublicCode(anyString())).willReturn(false);

            // when
            listener.handleMemberSignUp(event);

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
            MemberAccountProfileCreatedEvent event =
                    new MemberAccountProfileCreatedEvent(2L, "test@kakao.com");
            given(profileQueryService.existsByPublicCode(anyString()))
                    .willReturn(true)    // 첫 번째 생성 → 충돌
                    .willReturn(false);  // 두 번째 생성 → 성공

            // when
            listener.handleMemberSignUp(event);

            // then: existsByPublicCode가 두 번 호출되어야 한다
            then(profileQueryService).should(times(2)).existsByPublicCode(anyString());
            then(profileCommandService).should().save(any(Profile.class));
        }
    }
}

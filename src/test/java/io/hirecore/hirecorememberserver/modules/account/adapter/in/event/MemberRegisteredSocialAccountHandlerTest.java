package io.hirecore.hirecorememberserver.modules.account.adapter.in.event;

import com.navercorp.fixturemonkey.FixtureMonkey;
import io.hirecore.hirecorememberserver.modules.account.application.SocialAccountCommandService;
import io.hirecore.hirecorememberserver.modules.account.domain.SocialAccount;
import io.hirecore.hirecorememberserver.sharedkernel.domain.event.MemberRegisteredEvent;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.OAuth2Provider;
import io.hirecore.hirecorememberserver.support.FixtureMonkeyFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("MemberRegisteredSocialAccountHandler 단위 테스트")
@ExtendWith(MockitoExtension.class)
class MemberRegisteredSocialAccountHandlerTest {

    private final FixtureMonkey fm = FixtureMonkeyFactory.monkey();

    @InjectMocks
    private MemberRegisteredSocialAccountHandler handler;

    @Mock
    private SocialAccountCommandService socialAccountCommandService;

    @Captor
    private ArgumentCaptor<SocialAccount> socialAccountCaptor;

    @Test
    @DisplayName("소셜 연동 이벤트를 수신하면 SocialAccount를 생성하여 저장한다")
    void should_create_and_save_social_account_when_event_received() {
        // given
        MemberRegisteredEvent event = fm.giveMeBuilder(MemberRegisteredEvent.class)
                .set("provider", OAuth2Provider.KAKAO)
                .sample();

        given(socialAccountCommandService.saveSocialAccount(any(SocialAccount.class)))
                .willReturn(fm.giveMeOne(SocialAccount.class));

        // when
        handler.handleSocialAccountCreation(event);

        // then — 이벤트 필드가 SocialAccount에 정확히 매핑되었는지 검증
        then(socialAccountCommandService).should().saveSocialAccount(socialAccountCaptor.capture());

        SocialAccount captured = socialAccountCaptor.getValue();
        assertThat(captured.getMemberAccountId()).isEqualTo(event.memberAccountId());
        assertThat(captured.getProvider()).isEqualTo(event.provider());
        assertThat(captured.getProviderId()).isEqualTo(event.providerId());
        assertThat(captured.getEmail()).isEqualTo(event.email());
        assertThat(captured.getConnectedAt()).isEqualTo(event.socialConnectedAt());
        assertThat(captured.getEmailAgreed()).isEqualTo(event.socialEmailAgreed());
        assertThat(captured.getProfileNicknameAgreed()).isEqualTo(event.socialNicknameAgreed());
    }
}

package io.hirecore.hirecorememberserver.modules.account.application.usecase;

import io.hirecore.hirecorememberserver.common.application.port.out.TokenBlacklistPort;
import io.hirecore.hirecorememberserver.common.application.port.out.TokenExpirationResolverPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DisplayName("LogoutUseCaseImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
class LogoutUseCaseImplTest {

    @InjectMocks
    private LogoutUseCaseImpl logoutUseCase;

    @Mock
    private TokenBlacklistPort tokenBlacklistPort;

    @Mock
    private TokenExpirationResolverPort tokenExpirationResolverPort;

    @Test
    @DisplayName("토큰의 남은 만료 시간을 계산하여 블랙리스트에 등록한다")
    void execute_should_blacklist_token_with_remaining_millis() {
        // given
        String accessToken = "test-access-token";
        long remainingMillis = 3_600_000L; // 1시간
        given(tokenExpirationResolverPort.getRemainingMillis(accessToken)).willReturn(remainingMillis);

        // when
        logoutUseCase.execute(accessToken);

        // then
        verify(tokenExpirationResolverPort).getRemainingMillis(accessToken);
        verify(tokenBlacklistPort).blacklist(accessToken, remainingMillis);
    }

    @Test
    @DisplayName("남은 만료 시간이 0이면 블랙리스트에 0ms로 등록한다 (즉시 만료)")
    void execute_should_blacklist_with_zero_when_token_already_expired() {
        // given
        String accessToken = "expired-token";
        given(tokenExpirationResolverPort.getRemainingMillis(accessToken)).willReturn(0L);

        // when
        logoutUseCase.execute(accessToken);

        // then
        verify(tokenBlacklistPort).blacklist(accessToken, 0L);
    }
}

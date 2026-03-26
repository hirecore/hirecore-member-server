package io.hirecore.hirecorememberserver.modules.account.application.usecase;

import io.hirecore.hirecorememberserver.modules.account.application.MemberAccountCommandService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@DisplayName("LogoutUseCaseImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
class LogoutUseCaseImplTest {

    @InjectMocks
    private LogoutUseCaseImpl logoutUseCase;

    @Mock
    private MemberAccountCommandService memberAccountCommandService;

    @Test
    @DisplayName("회원의 토큰 버전을 증가시켜 기존 토큰을 무효화한다")
    void execute_should_increment_token_version() {
        // given
        Long memberId = 1L;

        // when
        logoutUseCase.execute(memberId);

        // then
        verify(memberAccountCommandService).incrementTokenVersion(memberId);
    }
}

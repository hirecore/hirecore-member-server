package io.hirecore.hirecorememberserver.modules.account.application.usecase;

import io.hirecore.hirecorememberserver.modules.account.application.port.out.ParseTokenPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.security.AuthPrincipal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.only;

@DisplayName("ResolveTokenUseCaseImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
class ResolveTokenUseCaseImplTest {

    @InjectMocks
    private ResolveTokenUseCaseImpl sut;

    @Mock
    private ParseTokenPort parseTokenPort;

    @Test
    @DisplayName("ParseTokenPort 에 토큰 문자열을 그대로 위임하고 결과 AuthPrincipal 을 반환한다")
    void should_delegate_to_parse_port() {
        // given
        String token = "jwt.access.token";
        AuthPrincipal expected = new AuthPrincipal(1L, "user@hirecore.io", "MEMBER", 7);
        given(parseTokenPort.parseToken(token)).willReturn(expected);

        // when
        AuthPrincipal result = sut.execute(token);

        // then
        assertThat(result).isSameAs(expected);
        then(parseTokenPort).should(only()).parseToken(token);
    }
}

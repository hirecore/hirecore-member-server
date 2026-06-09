package io.hirecore.hirecorememberserver.modules.account.application.usecase;

import io.hirecore.hirecorememberserver.modules.account.application.port.out.LoadTokenVersionPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@DisplayName("ValidateTokenVersionUseCaseImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
class ValidateTokenVersionUseCaseImplTest {

    @InjectMocks
    private ValidateTokenVersionUseCaseImpl sut;

    @Mock
    private LoadTokenVersionPort loadTokenVersionPort;

    @Test
    @DisplayName("현재 토큰 버전과 JWT 토큰 버전이 일치하면 true 를 반환한다")
    void should_return_true_when_versions_match() {
        // given
        given(loadTokenVersionPort.findCurrentTokenVersion(1L)).willReturn(Optional.of(7));

        // when
        boolean result = sut.execute(1L, 7);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("현재 토큰 버전과 JWT 토큰 버전이 다르면 false 를 반환한다 (로그아웃/무효화 토큰)")
    void should_return_false_when_versions_mismatch() {
        // given
        given(loadTokenVersionPort.findCurrentTokenVersion(1L)).willReturn(Optional.of(8));

        // when
        boolean result = sut.execute(1L, 7);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("회원의 토큰 버전을 찾을 수 없으면 false 를 반환한다 (존재하지 않는 회원)")
    void should_return_false_when_member_missing() {
        // given
        given(loadTokenVersionPort.findCurrentTokenVersion(999L)).willReturn(Optional.empty());

        // when
        boolean result = sut.execute(999L, 1);

        // then
        assertThat(result).isFalse();
    }
}

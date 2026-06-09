package io.hirecore.hirecorememberserver.modules.account.application.port.in;

/**
 * JWT 토큰에 포함된 token version 이 회원의 현재 토큰 버전과 일치하는지 검증하는 UseCase.
 *
 * <p>일치하지 않으면 로그아웃/강제 무효화된 토큰으로 간주한다.</p>
 */
public interface ValidateTokenVersionUseCase {
    boolean execute(Long memberId, int tokenVersion);
}

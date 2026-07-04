package io.hirecore.hirecorememberserver.modules.account.application.port.in;

// JWT token version 이 현재 버전과 일치하는지 검증 (불일치 시 무효 토큰)
public interface ValidateTokenVersionUseCase {
    boolean execute(Long memberId, int tokenVersion);
}

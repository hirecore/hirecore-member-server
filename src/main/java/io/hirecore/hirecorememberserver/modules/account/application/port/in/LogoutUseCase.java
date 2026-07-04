package io.hirecore.hirecorememberserver.modules.account.application.port.in;

// 로그아웃 (토큰 버전 증가로 기존 토큰 무효화)
public interface LogoutUseCase {

    void execute(Long memberId);
}

package io.hirecore.hirecorememberserver.modules.account.application.port.in;

/**
 * 로그아웃 유스케이스.
 *
 * <p>회원의 토큰 버전을 증가시켜 기존에 발급된 모든 토큰을 무효화합니다.</p>
 */
public interface LogoutUseCase {

    void execute(Long memberId);
}

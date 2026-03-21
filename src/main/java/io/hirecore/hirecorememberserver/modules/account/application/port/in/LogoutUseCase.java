package io.hirecore.hirecorememberserver.modules.account.application.port.in;

/**
 * 로그아웃 유스케이스.
 *
 * <p>액세스 토큰을 블랙리스트에 등록하여 재사용을 차단합니다.</p>
 */
public interface LogoutUseCase {

    void execute(String accessToken);
}

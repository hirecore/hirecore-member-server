package io.hirecore.hirecorememberserver.common.application.port.out;

/**
 * 토큰 블랙리스트 관리를 위한 출력 포트.
 *
 * <p>로그아웃된 토큰을 저장하고, 요청 시 블랙리스트 여부를 확인합니다.
 * 토큰의 남은 만료 시간을 TTL로 설정하여 만료 후 자동 삭제됩니다.</p>
 */
public interface TokenBlacklistPort {

    void blacklist(String token, long remainingMillis);

    boolean isBlacklisted(String token);
}

package io.hirecore.hirecorememberserver.common.application.port.out;

/**
 * 토큰의 남은 만료 시간을 조회하기 위한 출력 포트.
 *
 * <p>로그아웃 시 블랙리스트 TTL 설정에 사용됩니다.
 * 토큰의 만료 시간에서 현재 시간을 빼서 남은 밀리초를 반환합니다.</p>
 */
public interface TokenExpirationResolverPort {

    long getRemainingMillis(String token);
}

package io.hirecore.hirecorememberserver.modules.account.application.usecase;

import io.hirecore.hirecorememberserver.common.application.port.out.TokenBlacklistPort;
import io.hirecore.hirecorememberserver.common.application.port.out.TokenExpirationResolverPort;
import io.hirecore.hirecorememberserver.modules.account.application.port.in.LogoutUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 로그아웃 유스케이스 구현체.
 *
 * <p>액세스 토큰의 남은 만료 시간을 계산하여 Redis 블랙리스트에 등록합니다.
 * 블랙리스트에 등록된 토큰은 {@code JwtAuthenticationFilter}에서 인증이 거부되며,
 * TTL 만료 시 Redis에서 자동 삭제됩니다.</p>
 */
@Service
@RequiredArgsConstructor
public class LogoutUseCaseImpl implements LogoutUseCase {

    private final TokenBlacklistPort tokenBlacklistPort;
    private final TokenExpirationResolverPort tokenExpirationResolverPort;

    @Override
    public void execute(String accessToken) {
        long remainingMillis = tokenExpirationResolverPort.getRemainingMillis(accessToken);
        tokenBlacklistPort.blacklist(accessToken, remainingMillis);
    }
}

package io.hirecore.hirecorememberserver.modules.account.adapter.in.shared;

import io.hirecore.hirecorememberserver.modules.account.application.port.in.ResolveTokenUseCase;
import io.hirecore.hirecorememberserver.modules.account.application.port.in.ValidateTokenVersionUseCase;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.ResolveTokenPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.ValidateTokenVersionPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.security.AuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * common/security 의 필터 등 외부 호출자가 사용하는 sharedkernel 토큰 포트들의 단일 구현.
 *
 * <p>BC 당 단일 어댑터 정책에 따라 account 가 공개하는 sharedkernel 토큰 포트
 * ({@link ResolveTokenPort}, {@link ValidateTokenVersionPort}) 를 한 곳에서 구현한다.
 * 각 메서드는 BC 내부 UseCase 를 경유해 인프라 의존성을 추상화한다.</p>
 */
@Component
@RequiredArgsConstructor
public class TokenSharedQueryAdapter implements
        ResolveTokenPort,
        ValidateTokenVersionPort
{

    private final ResolveTokenUseCase resolveTokenUseCase;
    private final ValidateTokenVersionUseCase validateTokenVersionUseCase;

    @Override
    public AuthPrincipal resolveToken(String token) {
        return resolveTokenUseCase.execute(token);
    }

    @Override
    public boolean isValidTokenVersion(Long memberId, int tokenVersion) {
        return validateTokenVersionUseCase.execute(memberId, tokenVersion);
    }
}

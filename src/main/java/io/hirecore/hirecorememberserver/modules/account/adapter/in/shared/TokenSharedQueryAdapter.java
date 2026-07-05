package io.hirecore.hirecorememberserver.modules.account.adapter.in.shared;

import io.hirecore.hirecorememberserver.modules.account.application.port.in.ResolveTokenUseCase;
import io.hirecore.hirecorememberserver.modules.account.application.port.in.ValidateTokenVersionUseCase;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.ResolveTokenSharedPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.ValidateTokenVersionSharedPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.security.AuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// sharedkernel 토큰 포트 단일 구현 (BC당 단일 어댑터, 내부 UseCase 경유)
@Component
@RequiredArgsConstructor
public class TokenSharedQueryAdapter implements
        ResolveTokenSharedPort,
        ValidateTokenVersionSharedPort
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

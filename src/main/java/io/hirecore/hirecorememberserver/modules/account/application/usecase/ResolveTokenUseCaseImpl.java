package io.hirecore.hirecorememberserver.modules.account.application.usecase;

import io.hirecore.hirecorememberserver.modules.account.application.port.in.ResolveTokenUseCase;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.ParseTokenPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.security.AuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResolveTokenUseCaseImpl implements ResolveTokenUseCase {

    private final ParseTokenPort parseTokenPort;

    @Override
    public AuthPrincipal execute(String token) {
        return parseTokenPort.parseToken(token);
    }
}

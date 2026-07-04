package io.hirecore.hirecorememberserver.modules.account.application.port.in;

import io.hirecore.hirecorememberserver.sharedkernel.application.security.AuthPrincipal;

// JWT 로부터 AuthPrincipal 복원 (파싱은 ParseTokenPort 위임)
public interface ResolveTokenUseCase {
    AuthPrincipal execute(String token);
}

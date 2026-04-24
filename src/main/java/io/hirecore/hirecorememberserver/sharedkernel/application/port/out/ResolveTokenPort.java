package io.hirecore.hirecorememberserver.sharedkernel.application.port.out;

import io.hirecore.hirecorememberserver.sharedkernel.application.security.AuthPrincipal;

public interface ResolveTokenPort {
    AuthPrincipal resolveToken(String token);
}

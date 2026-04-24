package io.hirecore.hirecorememberserver.sharedkernel.application.port;

import io.hirecore.hirecorememberserver.sharedkernel.application.security.AuthPrincipal;

public interface TokenResolverPort {
    AuthPrincipal resolveToken(String token);
}

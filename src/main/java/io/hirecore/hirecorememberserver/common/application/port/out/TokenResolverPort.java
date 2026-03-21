package io.hirecore.hirecorememberserver.common.application.port.out;

import io.hirecore.hirecorememberserver.common.adapter.in.security.principal.AuthPrincipal;

public interface TokenResolverPort {
    AuthPrincipal resolveToken(String token);
}

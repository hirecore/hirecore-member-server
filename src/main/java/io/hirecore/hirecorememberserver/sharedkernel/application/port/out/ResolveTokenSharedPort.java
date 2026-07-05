package io.hirecore.hirecorememberserver.sharedkernel.application.port.out;

import io.hirecore.hirecorememberserver.sharedkernel.application.security.AuthPrincipal;

public interface ResolveTokenSharedPort {
    AuthPrincipal resolveToken(String token);
}

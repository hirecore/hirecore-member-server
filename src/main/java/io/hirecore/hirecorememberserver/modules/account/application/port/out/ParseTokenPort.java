package io.hirecore.hirecorememberserver.modules.account.application.port.out;

import io.hirecore.hirecorememberserver.sharedkernel.application.security.AuthPrincipal;

// JWT 파싱 out-port (인프라 의존 추상화)
public interface ParseTokenPort {
    AuthPrincipal parseToken(String token);
}

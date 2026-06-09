package io.hirecore.hirecorememberserver.modules.account.application.port.out;

import io.hirecore.hirecorememberserver.sharedkernel.application.security.AuthPrincipal;

/**
 * JWT 토큰 파싱을 위한 account BC 내부 out-port.
 *
 * <p>구현체는 JJWT 등 인프라 라이브러리에 의존한다. UseCase 가 인프라 의존성을 직접 갖지 않도록
 * 추상화한다.</p>
 */
public interface ParseTokenPort {
    AuthPrincipal parseToken(String token);
}

package io.hirecore.hirecorememberserver.modules.account.application.port.in;

import io.hirecore.hirecorememberserver.sharedkernel.application.security.AuthPrincipal;

/**
 * JWT 토큰 문자열로부터 인증 주체({@link AuthPrincipal}) 를 복원하는 UseCase.
 *
 * <p>토큰 파싱은 인프라 책임이라 내부 {@link io.hirecore.hirecorememberserver.modules.account.application.port.out.ParseTokenPort}
 * 에 위임한다.</p>
 */
public interface ResolveTokenUseCase {
    AuthPrincipal execute(String token);
}

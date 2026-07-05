package io.hirecore.hirecorememberserver.common.security.filter;

import io.hirecore.hirecorememberserver.sharedkernel.application.security.AuthPrincipal;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.ResolveTokenSharedPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.ValidateTokenVersionSharedPort;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;

/**
 * 요청 헤더의 JWT 토큰을 검증하여 사용자를 인증하는 필터입니다.
 * <p>
 * 요청 당 한 번 실행되며({@link OncePerRequestFilter}), 유효한 토큰이 존재할 경우
 * {@link SecurityContextHolder}에 인증 정보를 등록합니다.
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
    private static final String EXCEPTION_ATTRIBUTE = "exception";

    private final ResolveTokenSharedPort tokenResolverPort;
    private final ValidateTokenVersionSharedPort tokenVersionValidationPort;

    /**
     * JWT 토큰 검증 및 인증 처리를 수행합니다.
     * <p>
     * 토큰 검증 중 예외(만료, 위조 등)가 발생할 경우, 예외를 직접 던지지 않고
     * request 속성에 에러 메시지를 저장한 뒤 {@code filterChain.doFilter()}를 호출합니다.
     * 이는 이후 {@code AuthenticationEntryPoint}에서 해당 예외를 처리하도록 위임하기 위함입니다.
     * </p>
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = resolveToken(request);

        if (StringUtils.hasText(token)) {
            try {
                AuthPrincipal principal = tokenResolverPort.resolveToken(token);

                if (!tokenVersionValidationPort.isValidTokenVersion(principal.id(), principal.tokenVersion())) {
                    log.debug("Invalidated JWT token detected (token version mismatch) for user: '{}'", principal.id());
                    SecurityContextHolder.clearContext();
                    filterChain.doFilter(request, response);
                    return;
                }

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        principal, token, principal.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Security Context set for user: '{}'", authentication.getName());

            } catch (ExpiredJwtException e) {
                request.setAttribute(EXCEPTION_ATTRIBUTE, e.getMessage());
                log.debug("JWT Token expired: {}", e.getMessage());
                SecurityContextHolder.clearContext();

            } catch (JwtException | IllegalArgumentException e) {
                request.setAttribute(EXCEPTION_ATTRIBUTE, e.getMessage());
                log.warn("Invalid JWT processing: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 요청 쿠키에서 액세스 토큰 값을 추출합니다.
     */
    private String resolveToken(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, ACCESS_TOKEN_COOKIE_NAME);
        return (cookie != null) ? cookie.getValue() : null;
    }
}

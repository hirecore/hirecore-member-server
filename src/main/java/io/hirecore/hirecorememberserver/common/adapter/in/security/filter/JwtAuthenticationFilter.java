package io.hirecore.hirecorememberserver.common.adapter.in.security.filter;

import io.hirecore.hirecorememberserver.common.adapter.in.security.principal.AuthPrincipal;
import io.hirecore.hirecorememberserver.common.application.port.out.TokenBlacklistPort;
import io.hirecore.hirecorememberserver.common.application.port.out.TokenResolverPort;
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

    private final TokenResolverPort tokenResolverPort;
    private final TokenBlacklistPort tokenBlacklistPort;

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
            if (tokenBlacklistPort.isBlacklisted(token)) {
                log.debug("Blacklisted JWT token detected");
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }

            try {
                Authentication authentication = resolveAuthentication(token);
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

    /**
     * 토큰을 파싱하여 Spring Security 인증 객체({@link Authentication})를 생성합니다.
     */
    private Authentication resolveAuthentication(String token) {
        AuthPrincipal principal = tokenResolverPort.resolveToken(token);
        return new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
    }
}

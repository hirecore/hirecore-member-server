package io.hirecore.hirecorememberserver.modules.account.adapter.out.jwt;

import io.hirecore.hirecorememberserver.modules.account.domain.vo.MemberRole;
import io.hirecore.hirecorememberserver.sharedkernel.application.security.AuthPrincipal;
import io.hirecore.hirecorememberserver.common.security.properties.JwtProperties;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.IssueTokenPort;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.ParseTokenPort;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

// JJWT 기반 JWT 토큰 발급·파싱 어댑터 (HMAC-SHA 서명)
@Slf4j
@Component
public class TokenProviderAdapter implements
        ParseTokenPort,
        IssueTokenPort
{

    private final SecretKey key;
    private final long accessTokenExpirationMills;
    private final long refreshTokenExpirationMills;

    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_TOKEN_VERSION = "tv";

    public TokenProviderAdapter(JwtProperties jwtProperties) {
        byte[] keyBytes = jwtProperties.secret().getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpirationMills = jwtProperties.accessExpirationMillis();
        this.refreshTokenExpirationMills = jwtProperties.refreshExpirationMillis();
    }

    @Override
    public IssueTokenPort.Result issueTokenPair(IssueTokenPort.Request tokenClaims) {
        Instant now = Instant.now();

        String accessToken = generateToken(tokenClaims, now, accessTokenExpirationMills);
        String refreshToken = generateToken(tokenClaims, now, refreshTokenExpirationMills);

        return new IssueTokenPort.Result(accessToken, refreshToken);
    }

    @Override
    public AuthPrincipal parseToken(String token) {
        Claims claims = parseClaims(token);

        try {
            Long id = Long.parseLong(claims.getSubject());
            String email = claims.get(CLAIM_EMAIL, String.class);
            String roleStr = claims.get(CLAIM_ROLE, String.class);
            int tokenVersion = claims.get(CLAIM_TOKEN_VERSION, Integer.class);

            validateRole(roleStr);
            return new AuthPrincipal(id, email, roleStr, tokenVersion);

        } catch (NumberFormatException e) {
            log.warn("Invalid Subject (MemberId) format in JWT: {}", claims.getSubject());
            throw new JwtException("Invalid JWT Subject");
        }
    }

    private String generateToken(IssueTokenPort.Request claims, Instant now, long expirationMillis) {
        return Jwts.builder()
                .subject(String.valueOf(claims.id()))
                .claim(CLAIM_EMAIL, claims.email())
                .claim(CLAIM_ROLE, claims.role().name())
                .claim(CLAIM_TOKEN_VERSION, claims.tokenVersion())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expirationMillis, ChronoUnit.MILLIS)))
                .signWith(key)
                .compact();
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token: {}", e.getMessage());
            throw e;
        } catch (SecurityException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT signature or malformed token: {}", e.getMessage());
            throw new JwtException("이용할 수 없는 토큰입니다.", e);
        }
    }

    private void validateRole(String roleStr) {
        try {
            MemberRole.valueOf(roleStr);
        } catch (IllegalArgumentException | NullPointerException e) {
            log.error("Invalid Role found in JWT: {}", roleStr);
            throw new JwtException("이용할 수 없는 토큰입니다.");
        }
    }
}

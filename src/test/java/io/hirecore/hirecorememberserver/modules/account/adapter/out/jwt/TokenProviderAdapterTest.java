package io.hirecore.hirecorememberserver.modules.account.adapter.out.jwt;

import io.hirecore.hirecorememberserver.common.adapter.in.security.principal.AuthPrincipal;
import io.hirecore.hirecorememberserver.common.adapter.out.jwt.properties.JwtProperties;
import io.hirecore.hirecorememberserver.modules.account.application.port.in.dto.response.PairTokenResponse;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.dto.request.TokenClaimsRequest;
import io.hirecore.hirecorememberserver.modules.account.domain.vo.MemberRole;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("TokenProviderAdapter 단위 테스트")
class TokenProviderAdapterTest {

    // HMAC-SHA256 최소 키 길이: 32바이트(256비트)
    private static final String SECRET = "test-secret-key-for-jwt-hmac-sha-256-minimum-32-bytes";
    private static final long ACCESS_MILLIS = 3_600_000L;   // 1시간
    private static final long REFRESH_MILLIS = 86_400_000L; // 24시간

    private final TokenProviderAdapter adapter = new TokenProviderAdapter(
            new JwtProperties(SECRET, ACCESS_MILLIS, REFRESH_MILLIS)
    );

    private final TokenClaimsRequest validClaims = new TokenClaimsRequest(
            42L, "user@test.com", MemberRole.USER, 0
    );

    @Nested
    @DisplayName("issueTokenPair()")
    class IssueTokenPairTest {

        @Test
        @DisplayName("유효한 클레임으로 액세스·리프레시 토큰 쌍을 발급한다")
        void should_issue_access_and_refresh_token() {
            PairTokenResponse pair = adapter.issueTokenPair(validClaims);

            assertThat(pair.accessToken()).isNotBlank();
            assertThat(pair.refreshToken()).isNotBlank();
        }

        @Test
        @DisplayName("액세스 토큰과 리프레시 토큰은 서로 다른 값이다")
        void should_issue_distinct_access_and_refresh_tokens() {
            PairTokenResponse pair = adapter.issueTokenPair(validClaims);

            assertThat(pair.accessToken()).isNotEqualTo(pair.refreshToken());
        }

        @Test
        @DisplayName("모든 MemberRole에 대해 토큰을 발급할 수 있다")
        void should_issue_token_for_all_roles() {
            for (MemberRole role : MemberRole.values()) {
                TokenClaimsRequest claims = new TokenClaimsRequest(1L, "e@test.com", role, 0);
                PairTokenResponse pair = adapter.issueTokenPair(claims);

                assertThat(pair.accessToken()).isNotBlank();
            }
        }
    }

    @Nested
    @DisplayName("resolveToken()")
    class ResolveTokenTest {

        @Test
        @DisplayName("발급한 액세스 토큰에서 클레임(id, email, role)을 정확하게 파싱한다")
        void should_parse_claims_from_access_token() {
            PairTokenResponse pair = adapter.issueTokenPair(validClaims);

            AuthPrincipal principal = adapter.resolveToken(pair.accessToken());

            assertThat(principal.id()).isEqualTo(42L);
            assertThat(principal.email()).isEqualTo("user@test.com");
            assertThat(principal.role()).isEqualTo(MemberRole.USER.name());
            assertThat(principal.tokenVersion()).isZero();
        }

        @Test
        @DisplayName("발급한 리프레시 토큰에서도 클레임을 정확하게 파싱한다")
        void should_parse_claims_from_refresh_token() {
            PairTokenResponse pair = adapter.issueTokenPair(validClaims);

            AuthPrincipal principal = adapter.resolveToken(pair.refreshToken());

            assertThat(principal.id()).isEqualTo(42L);
            assertThat(principal.email()).isEqualTo("user@test.com");
        }

        @Test
        @DisplayName("만료된 토큰은 ExpiredJwtException을 발생시킨다")
        void should_throw_expired_jwt_exception_when_token_is_expired() {
            // given — 만료 시간을 0ms로 설정하여 즉시 만료되는 토큰 발급
            TokenProviderAdapter shortLivedAdapter = new TokenProviderAdapter(
                    new JwtProperties(SECRET, 0L, 0L)
            );
            PairTokenResponse pair = shortLivedAdapter.issueTokenPair(validClaims);

            // when & then
            assertThatThrownBy(() -> adapter.resolveToken(pair.accessToken()))
                    .isInstanceOf(ExpiredJwtException.class);
        }

        @Test
        @DisplayName("완전히 잘못된 형식의 토큰은 JwtException을 발생시킨다")
        void should_throw_jwt_exception_when_token_is_malformed() {
            assertThatThrownBy(() -> adapter.resolveToken("not.a.valid.jwt.token"))
                    .isInstanceOf(JwtException.class);
        }

        @Test
        @DisplayName("다른 키로 서명된 토큰은 JwtException을 발생시킨다")
        void should_throw_jwt_exception_when_token_signed_with_different_key() {
            // given — 다른 비밀키로 발급한 토큰
            TokenProviderAdapter otherAdapter = new TokenProviderAdapter(
                    new JwtProperties("completely-different-secret-key-for-test-32bytes!!", ACCESS_MILLIS, REFRESH_MILLIS)
            );
            PairTokenResponse pair = otherAdapter.issueTokenPair(validClaims);

            // when & then — 현재 어댑터(다른 키)로 검증하면 실패
            assertThatThrownBy(() -> adapter.resolveToken(pair.accessToken()))
                    .isInstanceOf(JwtException.class);
        }
    }
}

package io.hirecore.hirecorememberserver.modules.account.adapter.out.oauth2.kakao.dto.result;

import com.fasterxml.jackson.annotation.JsonProperty;

// 카카오 토큰 응답 (docs: rest-api#request-token-response-body)
public record KakaoTokenExternalResult(
        @JsonProperty("token_type")
        String tokenType,

        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("expires_in")
        Integer expiresIn,

        @JsonProperty("refresh_token")
        String refreshToken,

        @JsonProperty("refresh_token_expires_in")
        Integer refreshTokenExpiresIn
) {
}

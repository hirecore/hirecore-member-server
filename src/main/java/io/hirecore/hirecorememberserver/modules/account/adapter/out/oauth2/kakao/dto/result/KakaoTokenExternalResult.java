package io.hirecore.hirecorememberserver.modules.account.adapter.out.oauth2.kakao.dto.result;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @see <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-token-response-body"> Kakao의 응답데이터 레퍼런스 </a>
 * */
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

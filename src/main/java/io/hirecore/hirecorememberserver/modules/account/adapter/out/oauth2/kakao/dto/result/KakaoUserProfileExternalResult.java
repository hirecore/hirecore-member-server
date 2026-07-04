package io.hirecore.hirecorememberserver.modules.account.adapter.out.oauth2.kakao.dto.result;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.Instant;

// 카카오 사용자 정보 응답 (docs: rest-api#req-user-info-response)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoUserProfileExternalResult(
        Long id,
        Instant connectedAt,
        KakaoAccount kakaoAccount
) {
    // needsAgreement=false 가 동의됨을 의미 (카카오 정책)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record KakaoAccount(
            String email,
            Profile profile,
            boolean emailNeedsAgreement,
            boolean profileNicknameNeedsAgreement
    ) {
    }

    // 카카오 Profile 응답 (docs: rest-api#profile)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Profile(
            String nickname
    ) {
    }
}

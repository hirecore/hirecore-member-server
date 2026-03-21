package io.hirecore.hirecorememberserver.modules.account.adapter.out.oauth2.kakao.dto.result;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.Instant;

/**
 * @see <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#req-user-info-response">카카오 사용자 응답데이터</a>
 * */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoUserProfileExternalResult(
        Long id,
        Instant connectedAt,
        KakaoAccount kakaoAccount
) {
    /**
     * <h4> 주의 사항 </h4>
     * <ul>
     *     <li>동의항목 관련: 응답데이터가 헷갈릴 여지가 있습니다. 매개변수를 반드시 참고해주십시오. </li>
     * </ul>
     * @see <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#kakaoaccount"> 카카오 공식문서 : KakaoAccount 응답 데이터 </a>
     *
     * @param emailNeedsAgreement 사용자 이메일 통의 여부입니다. 카카오 문서 정책상 값이 false인 경우 동의가 되어졌음을 의미합니다.
     * @param profileNicknameNeedsAgreement 사용자 프로필 닉네임 통의 여부입니다. 카카오 문서 정책상 값이 false인 경우 동의가 되어졌음을 의미합니다.
     * */
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record KakaoAccount(
            String email,
            Profile profile,
            boolean emailNeedsAgreement,
            boolean profileNicknameNeedsAgreement
    ) {
    }

    /**
     * @see <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#profile"> 카카오 공식문서 : Profile 응답 데이터 </a>
     * */
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Profile(
            String nickname
    ) {
    }
}

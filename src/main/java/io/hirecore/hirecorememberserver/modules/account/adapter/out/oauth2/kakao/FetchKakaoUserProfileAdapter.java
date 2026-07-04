package io.hirecore.hirecorememberserver.modules.account.adapter.out.oauth2.kakao;

import io.hirecore.hirecorememberserver.modules.account.adapter.out.oauth2.kakao.exception.KakaoApiException;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.oauth2.kakao.mapper.KakaoUserProfileExternalDtoMapper;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.oauth2.kakao.properties.KakaoOAuth2Properties;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.dto.result.SocialUserProfileResult;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.FetchSocialUserProfilePort;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.oauth2.kakao.dto.result.KakaoTokenExternalResult;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.oauth2.kakao.dto.result.KakaoUserProfileExternalResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;


// 카카오 OAuth2 통신 어댑터 (RestClient 동기, 가상 스레드에 최적)
@Component
@RequiredArgsConstructor
public class FetchKakaoUserProfileAdapter implements FetchSocialUserProfilePort {

    private final RestClient kakaoTokenIssueRestClient;
    private final RestClient kakaoUserInfoRestClient;
    private final KakaoOAuth2Properties kakaoProperties;
    private final KakaoUserProfileExternalDtoMapper kakaoUserProfileExternalDtoMapper;

    // 카카오 인증 프로세스 오케스트레이션
    @Override
    public SocialUserProfileResult fetchByAuthorizationCode(String authorizationCode) {
        KakaoTokenExternalResult tokenResult = exchangeAuthorizationCodeForToken(authorizationCode);
        KakaoUserProfileExternalResult profileResult = fetchProfileFromKakao(tokenResult.accessToken());
        return kakaoUserProfileExternalDtoMapper.toSocialUserProfileResult(profileResult);
    }

    // 토큰 발급 (kauth 인증 서버)
    private KakaoTokenExternalResult exchangeAuthorizationCodeForToken(String authorizationCode) {
        return kakaoTokenIssueRestClient.post()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(buildTokenRequestForm(authorizationCode))
                .retrieve()
                .onStatus(status ->
                        status.is4xxClientError() || status.is5xxServerError(),
                        (request, response) -> {
                    throw new KakaoApiException(
                            "카카오 토큰 발급 실패 (HTTP " + response.getStatusCode().value() + ")",
                            response.getStatusCode().value());
                })
                .body(KakaoTokenExternalResult.class);
    }

    // 사용자 프로필 조회 (kapi API 서버)
    private KakaoUserProfileExternalResult fetchProfileFromKakao(String accessToken) {
        return kakaoUserInfoRestClient.get()
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .onStatus(status ->
                        status.is4xxClientError() || status.is5xxServerError(),
                        (request, response) -> {
                    throw new KakaoApiException(
                            "카카오 사용자 프로필 조회 실패 (HTTP " + response.getStatusCode().value() + ")",
                            response.getStatusCode().value());
                })
                .body(KakaoUserProfileExternalResult.class);
    }

    // 토큰 요청 폼 데이터 구성
    private MultiValueMap<String, String> buildTokenRequestForm(String authorizationCode) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", kakaoProperties.grantType());
        formData.add("client_id", kakaoProperties.clientId());
        formData.add("client_secret", kakaoProperties.clientSecret());
        formData.add("redirect_uri", kakaoProperties.clientRedirectUrl());
        formData.add("code", authorizationCode);
        return formData;
    }
}

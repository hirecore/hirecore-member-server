package io.hirecore.hirecorememberserver.modules.account.adapter.out.oauth2.kakao;

import io.hirecore.hirecorememberserver.modules.account.adapter.out.oauth2.kakao.exception.KakaoApiException;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.oauth2.kakao.mapper.KakaoUserProfileExternalDtoMapper;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.oauth2.kakao.properties.KakaoOAuth2Properties;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.dto.result.SocialUserProfileResult;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.LoadSocialUserProfilePort;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.oauth2.kakao.dto.result.KakaoTokenExternalResult;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.oauth2.kakao.dto.result.KakaoUserProfileExternalResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;


/**
 * 카카오 OAuth2 서버와 통신하여 정보를 조회하는 어댑터입니다.<br/>
 * [의의] RestClient를 사용하여 동기식으로 동작하며, 가상 스레드 환경에서 최상의 효율을 냅니다.
 */
@Component
@RequiredArgsConstructor
public class LoadKakaoUserProfileAdapter implements LoadSocialUserProfilePort {

    private final RestClient kakaoTokenIssueRestClient;
    private final RestClient kakaoUserInfoRestClient;
    private final KakaoOAuth2Properties kakaoProperties;
    private final KakaoUserProfileExternalDtoMapper kakaoUserProfileExternalDtoMapper;

    /**
     * 카카오 인증 프로세스를 오케스트레이션합니다.
     */
    @Override
    public SocialUserProfileResult load(String authorizationCode) {
        KakaoTokenExternalResult tokenResult = exchangeAuthorizationCodeForToken(authorizationCode);
        KakaoUserProfileExternalResult profileResult = fetchProfileFromKakao(tokenResult.accessToken());
        return kakaoUserProfileExternalDtoMapper.mapToSocialUserProfileResult(profileResult);
    }

    /**
     * 카카오 인증 서버(kauth)로부터 토큰을 발급받습니다.
     */
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

    /**
     * 카카오 API 서버(kapi)로부터 사용자 프로필을 요청합니다.
     */
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

    /**
     * 토큰 요청을 위한 폼 데이터를 구축합니다.
     */
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

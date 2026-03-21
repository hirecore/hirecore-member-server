package io.hirecore.hirecorememberserver.modules.account.adapter.out.oauth2.kakao.config;

import io.hirecore.hirecorememberserver.modules.account.adapter.out.oauth2.kakao.properties.KakaoOAuth2Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient; // 2026년 표준: RestClient 활용

/**
 * 카카오 API 통신을 위한 클라이언트 설정입니다.
 * [의의] 외부 API 기술(RestClient)을 도메인과 격리된 인프라 계층에서 관리합니다.
 */
@Configuration
@RequiredArgsConstructor
public class KakaoRestApiConfig {

    private final KakaoOAuth2Properties kakaoOAuth2Properties;

    /**
     * 카카오 인증 서버 전용 클라이언트 (토큰 발급용)
     */
    @Bean
    public RestClient kakaoTokenIssueRestClient() {
        return RestClient.builder()
                .baseUrl(kakaoOAuth2Properties.tokenIssueUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
    }

    /**
     * 카카오 리소스 서버 전용 클라이언트 (사용자 정보 조회용)
     */
    @Bean
    public RestClient kakaoUserInfoRestClient() {
        return RestClient.builder()
                .baseUrl(kakaoOAuth2Properties.userProfileUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
    }
}

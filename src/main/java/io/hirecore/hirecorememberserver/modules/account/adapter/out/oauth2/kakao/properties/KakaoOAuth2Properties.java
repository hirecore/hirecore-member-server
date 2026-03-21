package io.hirecore.hirecorememberserver.modules.account.adapter.out.oauth2.kakao.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hirecore.oauth2.kakao")
public record KakaoOAuth2Properties(
    String clientId,
    String clientSecret,
    String tokenIssueUrl,
    String userProfileUrl,
    String grantType,
    String clientRedirectUrl
) {
}

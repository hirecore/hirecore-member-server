package io.hirecore.hirecorememberserver.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hirecore.cookie")
public record AuthCookieProperties(
        boolean httpOnly,
        boolean secure,
        String sameSite
) {}

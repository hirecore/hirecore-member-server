package io.hirecore.hirecorememberserver.common.security.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hirecore.jwt")
public record JwtProperties(
        String secret,
        long accessExpirationMillis,
        long refreshExpirationMillis
){
}

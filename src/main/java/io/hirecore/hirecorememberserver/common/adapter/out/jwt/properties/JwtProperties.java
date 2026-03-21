package io.hirecore.hirecorememberserver.common.adapter.out.jwt.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hirecore.jwt")
public record JwtProperties(
        String secret,
        long accessExpirationMillis,
        long refreshExpirationMillis
){
}

package io.hirecore.hirecorememberserver.modules.file.adapter.out.cloudfront.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hirecore.object-storage.aws.cloudfront")
public record CloudFrontProperties(
        String publicBaseUrl
) {
}

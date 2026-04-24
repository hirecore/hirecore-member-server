package io.hirecore.hirecorememberserver.sharedkernel.infrastructure.aws;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("hirecore.object-storage.aws")
public record AwsRegionProperties (
        String region
){
}

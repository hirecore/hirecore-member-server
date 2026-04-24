package io.hirecore.hirecorememberserver.common.adapter.out.aws.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("hirecore.object-storage.aws")
public record AwsRegionProperties (
        String region
){
}

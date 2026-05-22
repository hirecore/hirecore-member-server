package io.hirecore.hirecorememberserver.sharedkernel.adapter.out.aws;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("hirecore.object-storage.aws")
public record AwsRegionProperties (
        String region
){
}

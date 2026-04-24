package io.hirecore.hirecorememberserver.modules.file.adapter.out.s3.config;

import io.hirecore.hirecorememberserver.common.adapter.out.aws.properties.AwsRegionProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {

    @Bean
    public S3Presigner s3Presigner(AwsRegionProperties awsRegionProperties) {
        return S3Presigner.builder()
                .region(Region.of(awsRegionProperties.region()))
                .build();
    }
}

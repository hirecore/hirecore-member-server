package io.hirecore.hirecorememberserver.modules.file.adapter.out.s3.config;

import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.aws.AwsRegionProperties;
import io.hirecore.hirecorememberserver.modules.file.adapter.out.s3.properties.S3Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
public class S3Config {

    @Bean
    public S3Presigner s3Presigner(AwsRegionProperties awsRegionProperties, S3Properties s3Properties) {
        S3Presigner.Builder builder = S3Presigner.builder()
                .region(Region.of(awsRegionProperties.region()));

        if (s3Properties.endpoint() != null) {
            builder.endpointOverride(URI.create(s3Properties.endpoint()))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(s3Properties.accessKey(), s3Properties.secretKey())
                    ));
        }

        if (Boolean.TRUE.equals(s3Properties.pathStyleAccess())) {
            builder.serviceConfiguration(S3Configuration.builder()
                    .pathStyleAccessEnabled(true)
                    .build());
        }

        return builder.build();
    }
}

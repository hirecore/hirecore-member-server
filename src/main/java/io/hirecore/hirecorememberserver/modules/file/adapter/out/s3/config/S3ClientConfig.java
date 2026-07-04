package io.hirecore.hirecorememberserver.modules.file.adapter.out.s3.config;

import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.aws.AwsRegionProperties;
import io.hirecore.hirecorememberserver.modules.file.adapter.out.s3.properties.S3Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

// 서버 직접 S3 호출용 S3Client 빈 (local 은 MinIO, dev/prod 는 기본 AWS 자격증명)
@Configuration
public class S3ClientConfig {

    @Bean
    public S3Client s3Client(AwsRegionProperties awsRegionProperties, S3Properties s3Properties) {
        S3ClientBuilder builder = S3Client.builder()
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

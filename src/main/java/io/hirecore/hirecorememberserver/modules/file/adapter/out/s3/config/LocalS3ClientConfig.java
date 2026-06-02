package io.hirecore.hirecorememberserver.modules.file.adapter.out.s3.config;

import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.aws.AwsRegionProperties;
import io.hirecore.hirecorememberserver.modules.file.adapter.out.s3.properties.S3Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

/**
 * 로컬 환경(MinIO)에서만 활성화되는 {@link S3Client} 빈.
 *
 * <p>일반적인 운영 흐름은 presigned URL 발급(즉 {@link software.amazon.awssdk.services.s3.presigner.S3Presigner})만으로 충분하지만,
 * 로컬 환경에서는 빌드 재시작 시 MinIO bucket 정리 등의 부수 작업이 필요하기 때문에
 * `S3Client` 자체를 사용하는 컴포넌트가 추가됩니다. dev/prod 에는 이 빈이 노출되지 않습니다.</p>
 */
@Configuration
@Profile({"local", "local-docker"})
public class LocalS3ClientConfig {

    @Bean
    public S3Client localS3Client(AwsRegionProperties awsRegionProperties, S3Properties s3Properties) {
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

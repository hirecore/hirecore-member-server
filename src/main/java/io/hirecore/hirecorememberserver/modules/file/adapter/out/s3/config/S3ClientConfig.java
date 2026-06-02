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

/**
 * 애플리케이션 전체에서 사용되는 {@link S3Client} 빈을 등록합니다.
 *
 * <p>일반 사용자 업로드는 presigned URL 발급({@link software.amazon.awssdk.services.s3.presigner.S3Presigner})으로
 * 처리되지만, ORPHANED 이미지 정리 스케줄러나 로컬 MinIO bucket 정리 등 서버가 직접 S3 호출을
 * 수행해야 하는 컴포넌트는 본 빈을 사용합니다.</p>
 *
 * <p>local/local-docker 환경에서는 MinIO endpoint 와 자격증명을 properties 로 주입받고,
 * dev/prod 환경에서는 endpoint 가 null 이므로 기본 AWS S3 endpoint 와 환경 자격증명 체인을 사용합니다.</p>
 */
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

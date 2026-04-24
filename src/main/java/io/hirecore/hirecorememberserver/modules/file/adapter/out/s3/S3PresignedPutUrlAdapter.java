package io.hirecore.hirecorememberserver.modules.file.adapter.out.s3;

import io.hirecore.hirecorememberserver.modules.file.adapter.out.cloudfront.properties.CloudFrontProperties;
import io.hirecore.hirecorememberserver.modules.file.adapter.out.s3.properties.S3Properties;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.GeneratePresignedPutUrlPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class S3PresignedPutUrlAdapter implements GeneratePresignedPutUrlPort {

    private final S3Presigner s3Presigner;
    private final S3Properties s3Properties;
    private final CloudFrontProperties cloudFrontProperties;

    @Override
    public String generate(String objectKey, String contentType, long contentLength) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Properties.bucket())
                .key(objectKey)
                .contentType(contentType)
                .contentLength(contentLength)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(s3Properties.presignedExpirationMinutes()))
                .putObjectRequest(putObjectRequest)
                .build();

        return s3Presigner.presignPutObject(presignRequest).url().toString();
    }

    @Override
    public String getBucketName() {
        return s3Properties.bucket();
    }

    @Override
    public String getPublicBaseUrl() {
        return cloudFrontProperties.publicBaseUrl();
    }
}

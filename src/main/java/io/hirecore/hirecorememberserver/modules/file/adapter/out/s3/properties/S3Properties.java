package io.hirecore.hirecorememberserver.modules.file.adapter.out.s3.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hirecore.object-storage.aws.s3")
public record S3Properties (
        Integer presignedExpirationMinutes,
        String bucket,
        Boolean pathStyleAccess,
        String endpoint,
        String accessKey,
        String secretKey
){
}

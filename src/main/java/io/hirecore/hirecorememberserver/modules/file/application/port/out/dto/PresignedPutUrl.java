package io.hirecore.hirecorememberserver.modules.file.application.port.out.dto;

public record PresignedPutUrl(
        String presignedUrl,
        String publicUrl,
        String bucketName
) {
}

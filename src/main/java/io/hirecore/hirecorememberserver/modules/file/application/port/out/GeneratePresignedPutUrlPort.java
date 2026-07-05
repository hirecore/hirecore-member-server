package io.hirecore.hirecorememberserver.modules.file.application.port.out;

public interface GeneratePresignedPutUrlPort {
    Result generate(String objectKey, String contentType, long contentLength);

    record Result(
            String presignedUrl,
            String publicUrl,
            String bucketName
    ) {
    }
}

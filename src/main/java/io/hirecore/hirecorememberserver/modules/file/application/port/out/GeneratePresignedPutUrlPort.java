package io.hirecore.hirecorememberserver.modules.file.application.port.out;

public interface GeneratePresignedPutUrlPort {
    String generate(String objectKey, String contentType, long contentLength);
    String getBucketName();
    String getPublicBaseUrl();
}

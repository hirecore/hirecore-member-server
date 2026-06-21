package io.hirecore.hirecorememberserver.modules.file.application.port.out;

import io.hirecore.hirecorememberserver.modules.file.application.port.out.dto.PresignedPutUrl;

public interface GeneratePresignedPutUrlPort {
    PresignedPutUrl generate(String objectKey, String contentType, long contentLength);
}

package io.hirecore.hirecorememberserver.modules.file.application.port.in.dto.response;

public record ImagePresignedPutUrlResponse (
    Long clientFileId,
    Long imageFileMetaId,
    String presignedUrl,
    String publicUrl
){
}

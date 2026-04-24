package io.hirecore.hirecorememberserver.modules.file.adapter.in.web.dto.response;

public record ImagePresignedPutUrlApiResponse(
        String clientFileId,
        Long imageFileMetaId,
        String presignedUrl,
        String publicUrl
) {
}

package io.hirecore.hirecorememberserver.modules.file.adapter.in.web.dto.response;

import io.hirecore.hirecorememberserver.common.web.json.TsidId;

public record ImagePresignedPutUrlApiResponse(
        String clientFileId,
        @TsidId Long imageFileMetaId,
        String presignedUrl,
        String publicUrl
) {
}

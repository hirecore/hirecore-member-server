package io.hirecore.hirecorememberserver.modules.file.adapter.in.web.dto.response;

import java.util.List;

public record ImagesPresignedPutUrlApiResponse(
        List<ImagePresignedPutUrlApiResponse> files
) {
}

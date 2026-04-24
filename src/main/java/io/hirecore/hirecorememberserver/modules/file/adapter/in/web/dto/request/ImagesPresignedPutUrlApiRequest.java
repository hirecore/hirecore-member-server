package io.hirecore.hirecorememberserver.modules.file.adapter.in.web.dto.request;

import jakarta.validation.Valid;

import java.util.List;

public record ImagesPresignedPutUrlApiRequest(
        @Valid List<ImagePresignedPutUrlApiRequest> files
) {
}

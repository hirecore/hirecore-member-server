package io.hirecore.hirecorememberserver.modules.file.application.port.in.dto.request;

import io.hirecore.hirecorememberserver.modules.file.domain.vo.DomainType;
import io.hirecore.hirecorememberserver.modules.file.domain.vo.FileExtension;
import io.hirecore.hirecorememberserver.modules.file.domain.vo.MimeType;
import io.hirecore.hirecorememberserver.modules.file.domain.vo.Purpose;

public record ImagePresignedPutUrlCommand(
        Long clientFileId,
        String originalFileName,
        MimeType mimeType,
        FileExtension fileExtension,
        Long fileSizeBytes,
        Integer width,
        Integer height,
        DomainType domainType,
        Purpose purpose
) {
}

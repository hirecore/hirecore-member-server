package io.hirecore.hirecorememberserver.modules.file.application.port.in;

import io.hirecore.hirecorememberserver.modules.file.domain.vo.FileExtension;
import io.hirecore.hirecorememberserver.modules.file.domain.vo.MimeType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.DomainType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Purpose;

import java.util.List;

public interface IssueImageUploadUrlUseCase {
    List<Response> execute(Long memberAccountId, List<Command> commandList);

    record Command(
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

    record Response(
            Long clientFileId,
            Long imageFileMetaId,
            String presignedUrl,
            String publicUrl
    ) {
    }
}

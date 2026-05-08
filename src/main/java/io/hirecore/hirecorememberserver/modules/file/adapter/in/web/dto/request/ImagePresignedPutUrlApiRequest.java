package io.hirecore.hirecorememberserver.modules.file.adapter.in.web.dto.request;

import io.hirecore.hirecorememberserver.modules.file.domain.vo.FileExtension;
import io.hirecore.hirecorememberserver.modules.file.domain.vo.MimeType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.DomainType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Purpose;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ImagePresignedPutUrlApiRequest(
        @NotNull(message = "클라이언트 파일 ID는 필수입니다.")
        Long clientFileId,

        @NotBlank(message = "원본 파일명은 필수입니다.")
        String originalFileName,

        @NotNull(message = "MIME 타입은 필수입니다.")
        MimeType mimeType,

        @NotNull(message = "파일 확장자는 필수입니다.")
        FileExtension fileExtension,

        @NotNull(message = "파일 크기는 필수입니다.")
        Long fileSizeBytes,

        @NotNull(message = "이미지 너비는 필수입니다.")
        Integer width,

        @NotNull(message = "이미지 높이는 필수입니다.")
        Integer height,

        @NotNull(message = "도메인 타입은 필수입니다.")
        DomainType domainType,

        @NotNull(message = "파일 업로드 목적은 필수입니다.")
        Purpose purpose
){
}

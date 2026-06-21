package io.hirecore.hirecorememberserver.modules.file.application.exception;

import io.hirecore.hirecorememberserver.sharedkernel.application.exception.ApplicationExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

public class FileApplicationExceptionCodeCluster {
    @Getter
    @RequiredArgsConstructor
    public enum DetailResponse implements ApplicationExceptionCode {
        UNSUPPORTED_MIME_TYPE(
                HttpStatus.BAD_REQUEST,
                "지원하지 않는 MimeType으로 업로드를 시도했습니다.",
                "지원하지 않는 파일 형식입니다."),

        IMAGE_OWNERSHIP_VIOLATION(
                HttpStatus.FORBIDDEN,
                "이미지 소유권자가 아닌 사용자가 상태변경을 시도했습니다.",
                "이미지 소유권자가 아닙니다. 정상적인 요청임에도 문제가 반복될 경우 관리자에게 문의해주세요."),

        IMAGE_NOT_FOUND(
                HttpStatus.NOT_FOUND,
                "요청에 포함된 imageFileMetaId 중 존재하지 않는 항목이 있습니다.",
                "요청에 포함된 이미지를 찾을 수 없습니다. 다시 시도해주세요."
        )
        ;

        private final HttpStatus httpStatus;
        private final String logMessage;
        private final String clientMessage;

        @Override
        public String getErrorCode() {
            return this.name();
        }
    }
}

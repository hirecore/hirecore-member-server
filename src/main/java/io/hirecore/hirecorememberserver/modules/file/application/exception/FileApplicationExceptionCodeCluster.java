package io.hirecore.hirecorememberserver.modules.file.application.exception;

import io.hirecore.hirecorememberserver.common.application.exception.ApplicationExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

public class FileApplicationExceptionCodeCluster {
    @Getter
    @RequiredArgsConstructor
    public enum DetailResponse implements ApplicationExceptionCode {
        STORAGE_QUOTA_EXCEEDED(
                HttpStatus.BAD_REQUEST,
                "업로드 요청 용량이 가용 스토리지를 초과했습니다.",
                "저장 공간이 부족합니다. 기존 파일을 삭제하거나 멤버십을 업그레이드해주세요."),

        UNSUPPORTED_MIME_TYPE(
                HttpStatus.BAD_REQUEST,
                "지원하지 않는 MimeType으로 업로드를 시도했습니다.",
                "지원하지 않는 파일 형식입니다.");

        private final HttpStatus httpStatus;
        private final String logMessage;
        private final String clientMessage;

        @Override
        public String getErrorCode() {
            return this.name();
        }
    }
}

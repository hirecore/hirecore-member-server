package io.hirecore.hirecorememberserver.modules.file.domain.exception;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.DomainExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

public class ImageFileMetaDomainExceptionCodeCluster {
    @Getter
    @RequiredArgsConstructor
    public enum HiddenDetailResponse implements DomainExceptionCode {
        // ID
        ID_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ImageFileMeta 도메인 객체 생성에서 id 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // MEMBER_ACCOUNT_ID
        MEMBER_ACCOUNT_ID_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ImageFileMeta 도메인 객체 생성에서 memberAccountId 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // DOMAIN_TYP
        DOMAIN_TYPE_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ImageFileMeta 도메인 객체 생성에서 domainType 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),
        
        // PURPOSE
        PURPOSE_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ImageFileMeta 도메인 객체 생성에서 purpose 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),
        
        // STORAGE_PROVIDER_MISSING
        STORAGE_PROVIDER_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ImageFileMeta 도메인 객체 생성에서 storage 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),
        
        // BUCKET_NAME
        BUCKET_NAME_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ImageFileMeta 도메인 객체 생성에서 bucketName 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // OBJECT_KEY
        OBJECT_KEY_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ImageFileMeta 도메인 객체 생성에서 objectKey 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // ORIGINAL_FILE_NAME
        ORIGINAL_FILE_NAME_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ImageFileMeta 도메인 객체 생성에서 originalFileName 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // CONTENT_TYPE
        CONTENT_TYPE_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ImageFileMeta 도메인 객체 생성에서 contentType 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // FILE_EXTENSION
        FILE_EXTENSION_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ImageFileMeta 도메인 객체 생성에서 fileExtension 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // FILE_SIZE_BYTES
        FILE_SIZE_BYTES_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ImageFileMeta 도메인 객체 생성에서 fileSizeBytes 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // WIDTH
        WIDTH_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ImageFileMeta 도메인 객체 생성에서 width 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // HEIGHT
        HEIGHT_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ImageFileMeta 도메인 객체 생성에서 height 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // UPLOAD_STATUS
        UPLOAD_STATUS_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ImageFileMeta 도메인 객체 생성에서 uploadStatus 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // AUDITING_INFO
        AUDITING_INFO_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ImageFileMeta 도메인 객체 생성에서 auditingInfo 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // MIME_TYPE
        UNSUPPORTED_MIME_TYPE(
                HttpStatus.BAD_REQUEST,
                "지원하지 않는 MimeType입니다.",
                "지원하지 않는 파일 형식입니다."),

        // FILE_EXTENSION
        UNSUPPORTED_FILE_EXTENSION(
                HttpStatus.BAD_REQUEST,
                "지원하지 않는 FileExtension입니다.",
                "지원하지 않는 파일 확장자입니다."),

        // DOMAIN_TYPE
        UNSUPPORTED_DOMAIN_TYPE(
                HttpStatus.BAD_REQUEST,
                "지원하지 않는 DomainType입니다.",
                "지원하지 않는 도메인 타입입니다."),

        // PURPOSE
        UNSUPPORTED_PURPOSE(
                HttpStatus.BAD_REQUEST,
                "지원하지 않는 Purpose입니다.",
                "지원하지 않는 파일 업로드 목적입니다.");

        private final HttpStatus httpStatus;
        private final String logMessage;
        private final String clientMessage;

        @Override
        public String getErrorCode() {
            return this.toString();
        }
    }
}

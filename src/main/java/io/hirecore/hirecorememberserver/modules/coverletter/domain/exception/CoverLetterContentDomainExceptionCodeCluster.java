package io.hirecore.hirecorememberserver.modules.coverletter.domain.exception;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.DomainExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

public class CoverLetterContentDomainExceptionCodeCluster {
    @Getter
    @RequiredArgsConstructor
    public enum HiddenDetailResponse implements DomainExceptionCode {
        // COVER_LETTER_ID
        COVER_LETTER_ID_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "CoverLetterContent 도메인 객체 생성에서 coverLetterId 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // CONTENT_JSON
        CONTENT_JSON_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "CoverLetterContent 도메인 객체 생성에서 contentJson 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // CONTENT_HTML
        CONTENT_HTML_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "CoverLetterContent 도메인 객체 생성에서 contentHtml 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // IMAGE_IDS
        IMAGE_IDS_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "CoverLetterContent 도메인 객체 생성에서 imageIds 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),
        IMAGE_IDS_CONTAINS_NULL(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "CoverLetterContent 의 imageIds 에 null 요소가 포함되어 있습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요.");

        private final HttpStatus httpStatus;
        private final String logMessage;
        private final String clientMessage;

        @Override
        public String getErrorCode() {
            return this.toString();
        }
    }
}

package io.hirecore.hirecorememberserver.modules.resume.domain.exception;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.DomainExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

public class ResumeTagDomainExceptionCodeCluster {
    @Getter
    @RequiredArgsConstructor
    public enum HiddenDetailResponse implements DomainExceptionCode {
        // ID
        ID_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ResumeTag 도메인 객체 생성에서 id 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // USER_INPUT_TAG
        USER_INPUT_TAG_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ResumeTag 도메인 객체 생성에서 userInputTag 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // NORMALIZED_TAG
        NORMALIZED_TAG_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ResumeTag 도메인 객체 생성에서 normalizedTag 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        ;

        private final HttpStatus httpStatus;
        private final String logMessage;
        private final String clientMessage;

        @Override
        public String getErrorCode() {
            return this.toString();
        }
    }
}

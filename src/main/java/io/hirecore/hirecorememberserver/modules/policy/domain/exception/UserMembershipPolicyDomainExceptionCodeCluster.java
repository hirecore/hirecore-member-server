package io.hirecore.hirecorememberserver.modules.policy.domain.exception;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.DomainExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

public class UserMembershipPolicyDomainExceptionCodeCluster {
    @Getter
    @RequiredArgsConstructor
    public enum HiddenDetailResponse implements DomainExceptionCode {
        // ID
        ID_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "UserMembershipPolicy 객체 생성에서 id 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),
        // CODE
        CODE_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "UserMembershipPolicy 객체 생성에서 code 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // NAME
        NAME_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "UserMembershipPolicy 객체 생성에서 name 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // CONTENT
        CONTENT_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "UserMembershipPolicy 객체 생성에서 content 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // AMOUNT
        AMOUNT_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "UserMembershipPolicy 객체 생성에서 amount 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // STORAGE_QUOTA_BYTES
        STORAGE_QUOTA_BYTES_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "UserMembershipPolicy 객체 생성에서 storageQuotaBytes 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // ACTIVE
        ACTIVE_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "UserMembershipPolicy 객체 생성에서 active 필드가 누락되었습니다.",
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

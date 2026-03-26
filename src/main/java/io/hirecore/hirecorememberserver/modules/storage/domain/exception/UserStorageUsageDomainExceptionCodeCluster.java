package io.hirecore.hirecorememberserver.modules.storage.domain.exception;

import io.hirecore.hirecorememberserver.common.domain.exception.DomainExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

public class UserStorageUsageDomainExceptionCodeCluster {

    @Getter
    @RequiredArgsConstructor
    public enum HiddenDetailResponse implements DomainExceptionCode {
        // ID
        ID_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "UserStorageUsage 도메인 객체 생성에서 id 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // MEMBER_ACCOUNT_ID
        MEMBER_ACCOUNT_ID_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "UserStorageUsage 도메인 객체 생성에서 memberAccountId 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // USED_QUOTA_BYTE
        USED_QUOTA_BYTE_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "UserStorageUsage 도메인 객체 생성에서 usedQuotaByte 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        USED_QUOTA_BYTE_NEGATIVE(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "UserStorageUsage 도메인 객체 생성에서 usedQuotaByte 필드가 음수입니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요.")
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

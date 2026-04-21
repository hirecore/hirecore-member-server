package io.hirecore.hirecorememberserver.modules.storage.domain.exception;

import io.hirecore.hirecorememberserver.common.domain.exception.DomainExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

public class UserStorageUsageLogDomainExceptionCodeCluster {

    @Getter
    @RequiredArgsConstructor
    public enum HiddenDetailResponse implements DomainExceptionCode {
        // ID
        ID_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "UserStorageUsageLog 도메인 객체 생성에서 id 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // MEMBER_ACCOUNT_ID
        MEMBER_ACCOUNT_ID_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "UserStorageUsageLog 도메인 객체 생성에서 memberAccountId 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // USAGE_CHANGE_REASON
        USAGE_CHANGE_REASON_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "UserStorageUsageLog 도메인 객체 생성에서 usageChangeReason 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // RESOURCE_KIND
        RESOURCE_KIND_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "UserStorageUsageLog 도메인 객체 생성에서 resourceKind 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // RESOURCE_KIND_ID
        RESOURCE_KIND_ID_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "UserStorageUsageLog 도메인 객체 생성에서 resourceKindId 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // CHANGE_BYTES
        CHANGE_BYTES_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "UserStorageUsageLog 도메인 객체 생성에서 changeBytes 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // BEFORE_USED_QUOTA_BYTES
        BEFORE_USED_QUOTA_BYTES_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "UserStorageUsageLog 도메인 객체 생성에서 beforeUsedQuotaBytes 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        BEFORE_USED_QUOTA_BYTES_NEGATIVE(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "UserStorageUsageLog 도메인 객체 생성에서 beforeUsedQuotaBytes 필드가 음수입니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // AFTER_USED_QUOTA_BYTES
        AFTER_USED_QUOTA_BYTES_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "UserStorageUsageLog 도메인 객체 생성에서 afterUsedQuotaBytes 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        AFTER_USED_QUOTA_BYTES_NEGATIVE(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "UserStorageUsageLog 도메인 객체 생성에서 afterUsedQuotaBytes 필드가 음수입니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // IDEMPOTENCY_KEY
        IDEMPOTENCY_KEY_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "UserStorageUsageLog 도메인 객체 생성에서 idempotencyKey 필드가 누락되었습니다.",
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

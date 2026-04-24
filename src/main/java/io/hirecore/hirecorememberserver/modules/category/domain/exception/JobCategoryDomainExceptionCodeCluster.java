package io.hirecore.hirecorememberserver.modules.category.domain.exception;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.DomainExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

public class JobCategoryDomainExceptionCodeCluster {
    @Getter
    @RequiredArgsConstructor
    public enum HiddenDetailResponse implements DomainExceptionCode {
        // ID
        JOB_CATEGORY_ID_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "JobCategory 도메인 객체 생성에서 id 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // CATEGORY_CODE
        CATEGORY_CODE_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "JobCategory 도메인 객체 생성에서 categoryCode 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // CATEGORY_NAME
        JOB_CATEGORY_NAME_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "JobCategory 도메인 객체 생성에서 categoryName 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // DEPTH
        JOB_CATEGORY_DEPTH_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "JobCategory 도메인 객체 생성에서 depth 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // IS_ACTIVE
        JOB_CATEGORY_IS_ACTIVE_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "JobCategory 도메인 객체 생성에서 isActive 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // IS_ASSIGNABLE
        JOB_CATEGORY_IS_ASSIGNABLE_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "JobCategory 도메인 객체 생성에서 isAssignable 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // ALLOWS_CUSTOM_INPUT
        JOB_CATEGORY_ALLOWS_CUSTOM_INPUT_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "JobCategory 도메인 객체 생성에서 allowsCustomInput 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // SORT_ORDER
        JOB_CATEGORY_SORT_ORDER_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "JobCategory 도메인 객체 생성에서 sortOrder 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // AUDITING_INFO
        JOB_CATEGORY_AUDITING_INFO_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "JobCategory 도메인 객체 생성에서 auditingInfo 필드가 누락되었습니다.",
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

package io.hirecore.hirecorememberserver.modules.portfolio.domain.exception;

import io.hirecore.hirecorememberserver.common.domain.exception.DomainExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

public class PortfolioJobCategoryDomainExceptionCodeCluster {
    @Getter
    @RequiredArgsConstructor
    public enum HiddenDetailResponse implements DomainExceptionCode {
        // ID
        ID_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "PortfolioJobCategory 도메인 객체 생성에서 id 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // PORTFOLIO_ID
        PORTFOLIO_ID_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "PortfolioJobCategory 도메인 객체 생성에서 portfolioId 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // JOB_CATEGORY_ID
        JOB_CATEGORY_ID_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "PortfolioJobCategory 도메인 객체 생성에서 jobCategoryId 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // IS_DELETED
        IS_DELETED_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "PortfolioJobCategory 도메인 객체 생성에서 isDeleted 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // CONNECTED_AT
        CONNECTED_AT_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "PortfolioJobCategory 도메인 객체 생성에서 connectedAt 필드가 누락되었습니다.",
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

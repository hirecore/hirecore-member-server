package io.hirecore.hirecorememberserver.modules.portfolio.domain.exception;

import io.hirecore.hirecorememberserver.common.domain.exception.DomainExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

public class PortfolioContentDomainExceptionCodeCluster {
    @Getter
    @RequiredArgsConstructor
    public enum HiddenDetailResponse implements DomainExceptionCode {
        // PORTFOLIO_ID
        PORTFOLIO_ID_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "PortfolioContent 도메인 객체 생성에서 portfolioId 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // CONTENT_JSON
        CONTENT_JSON_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "PortfolioContent 도메인 객체 생성에서 contentJson 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // CONTENT_HTML
        CONTENT_HTML_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "PortfolioContent 도메인 객체 생성에서 contentHtml 필드가 누락되었습니다.",
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

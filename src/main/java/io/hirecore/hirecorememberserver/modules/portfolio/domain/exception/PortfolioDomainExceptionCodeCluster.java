package io.hirecore.hirecorememberserver.modules.portfolio.domain.exception;

import io.hirecore.hirecorememberserver.common.domain.exception.DomainExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

public class PortfolioDomainExceptionCodeCluster {
    @Getter
    @RequiredArgsConstructor
    public enum HiddenDetailResponse implements DomainExceptionCode {
        // ID
        ID_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Portfolio 도메인 객체 생성에서 id 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // MEMBER_ACCOUNT_ID
        MEMBER_ACCOUNT_ID_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Portfolio 도메인 객체 생성에서 memberAccountId 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // PORTFOLIO_CATEGORY_ID
        PORTFOLIO_CATEGORY_ID_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Portfolio 도메인 객체 생성에서 portfolioCategoryId 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // TITLE
        TITLE_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Portfolio 도메인 객체 생성에서 title 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // PREVIEW_SUMMARY
        PREVIEW_SUMMARY_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Portfolio 도메인 객체 생성에서 previewSummary 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // PORTFOLIO_CONTENT
        PORTFOLIO_CONTENT_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Portfolio 도메인 객체 생성에서 portfolioContent 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // STATUS
        STATUS_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Portfolio 도메인 객체 생성에서 status 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // VISIBILITY
        VISIBILITY_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Portfolio 도메인 객체 생성에서 visibility 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // UPDATED_AT
        UPDATED_AT_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Portfolio 도메인 객체 생성에서 updatedAt 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // AUDITING_INFO
        AUDITING_INFO_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Portfolio 도메인 객체 생성에서 auditingInfo 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // PortfolioContent - ID
        PORTFOLIO_CONTENT_ID_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "PortfolioContent 도메인 객체 생성에서 id 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // PortfolioContent - PORTFOLIO_ID
        PORTFOLIO_CONTENT_PORTFOLIO_ID_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "PortfolioContent 도메인 객체 생성에서 portfolioId 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // PortfolioContent - CONTENT_JSON
        PORTFOLIO_CONTENT_JSON_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "PortfolioContent 도메인 객체 생성에서 contentJson 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // PortfolioContent - CONTENT_HTML
        PORTFOLIO_CONTENT_HTML_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "PortfolioContent 도메인 객체 생성에서 contentHtml 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // PortfolioContent 본문 변경 시 연관 엔티티 부재
        PORTFOLIO_CONTENT_NOT_ATTACHED(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Portfolio의 본문 변경 시점에 연관된 portfolioContent가 존재하지 않습니다.",
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

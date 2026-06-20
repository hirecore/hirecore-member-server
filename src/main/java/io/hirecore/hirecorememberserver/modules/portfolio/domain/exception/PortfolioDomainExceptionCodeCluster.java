package io.hirecore.hirecorememberserver.modules.portfolio.domain.exception;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.DomainExceptionCode;
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

        // PORTFOLIO_JOB_CATEGORY
        PORTFOLIO_JOB_CATEGORY_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Portfolio 도메인 객체 생성에서 portfolioJobCategory 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // TITLE
        TITLE_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Portfolio 도메인 객체 생성에서 title 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        TITLE_TOO_LONG(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Portfolio 도메인 객체 생성에서 title 길이가 허용 한도를 초과했습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // PREVIEW_SUMMARY
        PREVIEW_SUMMARY_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Portfolio 도메인 객체 생성에서 previewSummary 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        PREVIEW_SUMMARY_TOO_LONG(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Portfolio 도메인 객체 생성에서 previewSummary 길이가 허용 한도를 초과했습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // PRIVATE_MEMO
        PRIVATE_MEMO_TOO_LONG(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Portfolio 도메인 객체 생성에서 privateMemo 길이가 허용 한도를 초과했습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // EXTERNAL_LINKS
        EXTERNAL_LINKS_TOO_MANY(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Portfolio 도메인 객체 생성에서 externalLinks 개수가 허용 한도를 초과했습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // PORTFOLIO_TAGS
        PORTFOLIO_TAGS_TOO_MANY(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Portfolio 도메인 객체 생성에서 portfolioTags 개수가 허용 한도를 초과했습니다.",
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

        // COLLABORATION_TYPE
        COLLABORATION_TYPE_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Portfolio 도메인 객체 생성에서 collaborationType 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // EXTERNAL_LABEL
        EXTERNAL_LABEL_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "External vo 객체 생성에서 externalLabel 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."
        ),
        ;

        private final HttpStatus httpStatus;
        private final String logMessage;
        private final String clientMessage;

        @Override
        public String getErrorCode() {
            return this.toString();
        }
    }

    @Getter
    @RequiredArgsConstructor
    public enum DetailResponse implements DomainExceptionCode {
        INTEREST_OWNER_NOT_ALLOWED(
                HttpStatus.FORBIDDEN,
                "포트폴리오 소유자 본인의 관심 등록 시도입니다.",
                "본인이 등록한 포트폴리오에는 관심을 등록할 수 없습니다."),

        INTEREST_PORTFOLIO_FORBIDDEN(
                HttpStatus.FORBIDDEN,
                "비공개 포트폴리오에 대한 관심 등록 시도입니다.",
                "비공개 포트폴리오에는 관심을 등록할 수 없습니다."),

        PORTFOLIO_DELETE_DENIED(
                HttpStatus.FORBIDDEN,
                "본인 소유가 아닌 포트폴리오에 대한 변경/삭제 시도입니다.",
                "본인이 등록한 포트폴리오에 대해서만 변경 또는 삭제할 수 있습니다."),
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

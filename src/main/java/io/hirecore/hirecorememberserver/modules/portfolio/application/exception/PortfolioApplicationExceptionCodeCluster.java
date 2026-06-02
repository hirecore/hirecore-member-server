package io.hirecore.hirecorememberserver.modules.portfolio.application.exception;

import io.hirecore.hirecorememberserver.sharedkernel.application.exception.ApplicationExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

public class PortfolioApplicationExceptionCodeCluster {
    @Getter
    @RequiredArgsConstructor
    public enum DetailResponse implements ApplicationExceptionCode {
        // 4XX
        // CATEGOTY_CODE
        CATEGORY_CODE_MISSING(
                HttpStatus.BAD_REQUEST,
                "CreatePortfolioCommand 생성 실패: categoryCode 값이 공백이거나 누락되었습니다.",
                "직무 카테고리 코드가 공백이거나 누락되었습니다."),

        // COLLABORATION_TYPE
        COLLABORATION_TYPE_MISSING(
                HttpStatus.BAD_REQUEST,
                "CreatePortfolioCommand 생성 실패: collaborationType 값이 누락되었습니다.",
                "협업 유형이 누락되었습니다."),

        // VISIBILITY
        VISIBILITY_MISSING(
                HttpStatus.BAD_REQUEST,
                "CreatePortfolioCommand 생성 실패: visibility 값이 누락되었습니다.",
                "공개 범위가 누락되었습니다."),

        // TITLE
        TITLE_MISSING(
                HttpStatus.BAD_REQUEST,
                "CreatePortfolioCommand 생성 실패: title 값이 공백이거나 누락되었습니다.",
                "제목이 공백이거나 누락되었습니다."),

        // CONTEN
        CONTENT_MISSING(
                HttpStatus.BAD_REQUEST,
                "CreatePortfolioCommand 생성 실패: content 값이 누락되었습니다.",
                "포트폴리오 본문이 누락되었습니다."),

        // CONTENT_JSON
        CONTENT_JSON_MISSING(
                HttpStatus.BAD_REQUEST,
                "PortfolioContentCommand 생성 실패: json 값이 누락되었습니다.",
                "포트폴리오 본문 JSON이 누락되었습니다."),

        // CONTENT_HTML
        CONTENT_HTML_MISSING(
                HttpStatus.BAD_REQUEST,
                "PortfolioContentCommand 생성 실패: html 값이 공백이거나 누락되었습니다.",
                "포트폴리오 본문 HTML이 공백이거나 누락되었습니다."),

        // NICKNAME
        NICKNAME_NOT_FOUND(
                HttpStatus.NOT_FOUND,
                "포트폴리오 작성자의 닉네임이 존재하지 않습니다.",
                "포트폴리오 작성자의 닉네임을 불러올 수 없습니다. 존재하지 않습니다."),

        // JOB_CATEGORY
        JOB_CATEGORY_NOT_FOUND(
                HttpStatus.NOT_FOUND,
                "포트폴리오에 해당하는 직무 카테고리가 존재하지 않습니다.",
                "포트폴리오에 해당하는 직무 카테고리를 찾을 수 없습니다. 관리자에게 문의해주세요."),

        // PORTFOLIO
        PORTFOLIO_NOT_FOUND(
                HttpStatus.NOT_FOUND,
                "포트폴리오를 찾을 수 없습니다.",
                "요청한 포트폴리오가 존재하지 않습니다."),

        PORTFOLIO_FORBIDDEN(
                HttpStatus.FORBIDDEN,
                "포트폴리오 접근 권한이 없습니다.",
                "요청한 포트폴리오가 비공개이거나 접근할 권한이 없습니다."),

        PORTFOLIO_NICKNAME_NOT_FOUND(
                HttpStatus.NOT_FOUND,
                "포트폴리오 작성자의 닉네임을 불러올 수 없습니다.",
                "포트폴리오 작성자의 닉네임을 불러올 수 없습니다. 관리자에게 문의해주세요."),
        // 5XX
        // CONTENT_JSON
        CONTENT_JSON_SERIALIZATION_FAILED(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "포트폴리오 본문 JSON을 직렬화하는 데 실패했습니다.",
                "포트폴리오 등록 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
        ;

        private final HttpStatus httpStatus;
        private final String logMessage;
        private final String clientMessage;

        @Override
        public String getErrorCode() {
            return this.name();
        }
    }
}

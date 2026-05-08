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
        CATEGORY_CODE_MISSING(
                HttpStatus.BAD_REQUEST,
                "CreatePortfolioCommand 생성 실패: categoryCode 값이 공백이거나 누락되었습니다.",
                "직무 카테고리 코드가 공백이거나 누락되었습니다."),

        COLLABORATION_TYPE_MISSING(
                HttpStatus.BAD_REQUEST,
                "CreatePortfolioCommand 생성 실패: collaborationType 값이 누락되었습니다.",
                "협업 유형이 누락되었습니다."),

        VISIBILITY_MISSING(
                HttpStatus.BAD_REQUEST,
                "CreatePortfolioCommand 생성 실패: visibility 값이 누락되었습니다.",
                "공개 범위가 누락되었습니다."),

        TITLE_MISSING(
                HttpStatus.BAD_REQUEST,
                "CreatePortfolioCommand 생성 실패: title 값이 공백이거나 누락되었습니다.",
                "제목이 공백이거나 누락되었습니다."),

        CONTENT_MISSING(
                HttpStatus.BAD_REQUEST,
                "CreatePortfolioCommand 생성 실패: content 값이 누락되었습니다.",
                "포트폴리오 본문이 누락되었습니다."),

        CONTENT_JSON_MISSING(
                HttpStatus.BAD_REQUEST,
                "PortfolioContentCommand 생성 실패: json 값이 누락되었습니다.",
                "포트폴리오 본문 JSON이 누락되었습니다."),

        CONTENT_HTML_MISSING(
                HttpStatus.BAD_REQUEST,
                "PortfolioContentCommand 생성 실패: html 값이 공백이거나 누락되었습니다.",
                "포트폴리오 본문 HTML이 공백이거나 누락되었습니다.");

        private final HttpStatus httpStatus;
        private final String logMessage;
        private final String clientMessage;

        @Override
        public String getErrorCode() {
            return this.name();
        }
    }
}

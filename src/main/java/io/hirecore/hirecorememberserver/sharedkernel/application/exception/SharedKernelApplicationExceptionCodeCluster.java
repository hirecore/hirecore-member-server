package io.hirecore.hirecorememberserver.sharedkernel.application.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

public class SharedKernelApplicationExceptionCodeCluster {

    @Getter
    @RequiredArgsConstructor
    public enum DetailResponse implements ApplicationExceptionCode {
        // CURSOR (sharedkernel/application/cursor 의 무한 스크롤 cursor 토큰)
        INFINITE_SCROLL_CURSOR_INVALID(
                HttpStatus.BAD_REQUEST,
                "무한 스크롤 cursor 토큰 디코딩 실패: 토큰 형식이 올바르지 않습니다.",
                "요청한 페이지 정보가 올바르지 않습니다. 처음부터 다시 시도해주세요."),
        ;

        private final HttpStatus httpStatus;
        private final String logMessage;
        private final String clientMessage;

        @Override
        public String getErrorCode() {
            return this.name();
        }
    }

    @Getter
    @RequiredArgsConstructor
    public enum HiddenDetailResponse implements ApplicationExceptionCode {
        MEMBER_ACCOUNT_ID_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "memberAccountId가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),
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

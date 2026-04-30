package io.hirecore.hirecorememberserver.sharedkernel.application.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

public class SharedKernelApplicationExceptionCodeCluster {

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

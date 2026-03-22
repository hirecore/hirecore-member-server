package io.hirecore.hirecorememberserver.modules.profile.domain.exception;

import io.hirecore.hirecorememberserver.common.domain.exception.DomainExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

public class UserProfileDomainExceptionCodeCluster {
    @Getter
    @RequiredArgsConstructor
    public enum HiddenDetailResponse implements DomainExceptionCode {
        // MARKETING_EMAIL
        MARKETING_EMAIL_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "UserProfile 도메인 객체 생성에서 marketingEmail필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."
        )
        ;

        private final HttpStatus httpStatus;
        private final String logMessage;
        private final String clientMessage;

        public String getErrorCode() {
            return this.toString();
        }
    }
}

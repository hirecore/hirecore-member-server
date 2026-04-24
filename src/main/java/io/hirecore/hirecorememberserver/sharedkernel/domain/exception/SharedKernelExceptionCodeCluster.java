package io.hirecore.hirecorememberserver.sharedkernel.domain.exception;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.DomainExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

public class SharedKernelExceptionCodeCluster {

    @Getter
    @RequiredArgsConstructor
    public enum HiddenDetailResponse implements DomainExceptionCode {
        // MEMBER_REGISTERED_EVENT
        MEMBER_ACCOUNT_ID_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "MemberRegisteredEvent 생성에서 memberAccountId가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        PROVIDER_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "MemberRegisteredEvent 생성에서 provider가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        PROVIDER_ID_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "MemberRegisteredEvent 생성에서 providerId가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        EMAIL_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "MemberRegisteredEvent 생성에서 email이 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        SOCIAL_CONNECTED_AT_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "MemberRegisteredEvent 생성에서 socialConnectedAt이 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // OAUTH2_PROVIDER
        OAUTH2_PROVIDER_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "OAuth2Provider 변환 실패: provider 값이 공백이거나 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        OAUTH2_PROVIDER_INVALID(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "OAuth2Provider 변환 실패: 지원하지 않는 provider 값입니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        AUDITING_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "도메인 객체 생성 시 audtingInfo 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),
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

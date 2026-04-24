package io.hirecore.hirecorememberserver.modules.account.domain.exception;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.DomainExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

public class SocialAccountDomainExceptionCodeCluster {
    @Getter
    @RequiredArgsConstructor
    public enum HiddenDetailResponse implements DomainExceptionCode {
        // ID
        ID_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "SocialAccount 객체 생성에서 id 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // PROVIDER
        PROVIDER_INVALID(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "지원하지 않는 소셜 로그인 플랫폼으로 접근했습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),
        PROVIDER_MISMATCH(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "요청된 프로바이더와 실제 데이터가 불일치합니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),
        PROVIDER_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "소셜 플랫폼 정보의 값이 공백이거나 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // MEMBER_ACCOUNT_ID
        MEMBER_ACCOUNT_ID_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "연관된 회원 계정 ID가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // PROVIDER_ID
        PROVIDER_ID_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "소셜 계정 식별값(ID)이 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // CONNECTED_AT
        CONNECTED_AT_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "소셜 플랫폼이 서비스에 연결된 시간이 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."
        ),

        // EMAIL
        EMAIL_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "소셜 계정의 이메일 정보가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // NAME
        NICK_NAME_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "소셜 계정의 닉네임 정보가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // CONSENT_INFO
        CONSENT_INFO_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "소셜 계정의 동의항목 정보가 누락되었습니다.",
                "서버 내부에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // USER_PROFILE
        USER_PROFILE_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "소셜 사용자 정보가 없습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요.")
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
        //PROVIDER
        PROVIDER_INVALID(
                HttpStatus.BAD_REQUEST,
                "지원하지 않는 소셜 로그인 플랫폼으로 접근했습니다.",
                "지원하지 않는 소셜 로그인 플랫폼으로 접근했습니다."),
        PROVIDER_MISSING(
                HttpStatus.BAD_REQUEST,
                "소셜 플랫폼 정보의 값이 공백이거나 누락되었습니다.",
                "소셜 플랫폼 정보의 값이 공백이거나 누락되었습니다."),
        ;

        private final HttpStatus httpStatus;
        private final String logMessage;
        private final String clientMessage;

        public String getErrorCode() {
            return this.toString();
        }
    }
}

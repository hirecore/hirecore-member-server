package io.hirecore.hirecorememberserver.modules.account.domain.exception;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.DomainExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

public class MemberAccountDomainExceptionCodeCluster {
    @Getter
    @RequiredArgsConstructor
    public enum HiddenDetailResponse implements DomainExceptionCode {
        // ID
        ID_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "MemberAccount 객체 생성에서 id 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // EMAIL
        EMAIL_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "MemberAccount 객체 생성에서 email필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // ROLE
        ROLE_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "MemberAccount 객체 생성에서 role필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // SOCIAL_LINK
        SOCIAL_LINK_WITHOUT_EMAIL_AGREED(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "소셜 연동 요청 시 emailAgreed 정보가 false입니다. (제공 소셜 프로바이더의 이메일 수집 동의가 되지 않았습니다.)",
                        "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        SOCIAL_LINK_WITHOUT_PROFILE_NICKNAME_AGREED(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "소셜 연동 요청 시 profileNicknameAgreed 정보가 false입니다. (제공 소셜 프로바이더의 프로필 닉네임 수집 동의가 되지 않았습니다.)",
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

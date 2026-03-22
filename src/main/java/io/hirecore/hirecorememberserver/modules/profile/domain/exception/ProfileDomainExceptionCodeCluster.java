package io.hirecore.hirecorememberserver.modules.profile.domain.exception;

import io.hirecore.hirecorememberserver.common.domain.exception.DomainExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

public class ProfileDomainExceptionCodeCluster {

    @Getter
    @RequiredArgsConstructor
    public enum HiddenDetailResponse implements DomainExceptionCode {
        // ID
        ID_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Profile 도메인 객체 생성에서 id 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // MEMBER_ACCOUNT_ID
        MEMBER_ACCOUNT_ID_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Profile 도메인 객체 생성에서 memberAccountId필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // NICKNAME
        NICKNAME_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Profile 도메인 객체 생성에서 nickname필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요.")
        ,

        // VO
        // PUBLIC_CODE_INFO
        PUBLIC_CODE_INFO_NULL(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Profile 도메인 객체 생성에서 publicCodeInfo 필드가 누락되었습니다.",
                        "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        PUBLIC_CODE_VALUE_INVALID(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "PublicCodeInfo의 publicCode 값이 null이거나 올바른 형식(8자리 영숫자)이 아닙니다.",
                        "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // Domain
        // USER_PROFILE
        USER_PROFILE_NULL(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Profile 도메인 객체 생성에서 userProfile 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요.")
        ;

        private final HttpStatus httpStatus;
        private final String logMessage;
        private final String clientMessage;

        public String getErrorCode() {
            return this.toString();
        }
    }
}

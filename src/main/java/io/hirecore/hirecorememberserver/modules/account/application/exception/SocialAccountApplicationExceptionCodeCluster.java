package io.hirecore.hirecorememberserver.modules.account.application.exception;

import io.hirecore.hirecorememberserver.common.application.exception.ApplicationExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

public class SocialAccountApplicationExceptionCodeCluster {
    @Getter
    @RequiredArgsConstructor
    public enum DetailResponse implements ApplicationExceptionCode {
        // 4XX
        PROVIDER_MISSING(
                HttpStatus.BAD_REQUEST,
                "SocialLoginCommand 생성 실패: provider 값이 공백이거나 누락되었습니다.",
                "소셜 플랫폼 정보의 값이 공백이거나 누락되었습니다."),

        AUTHORIZATION_CODE_MISSING(
                HttpStatus.BAD_REQUEST,
                "SocialLoginCommand 생성 실패: authorizationCode 값이 공백이거나 누락되었습니다.",
                "소셜 인가 코드의 값이 공백이거나 누락되었습니다."),

        // 5XX
        USER_PROFILE_PROVIDER_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "SocialUserProfile 생성 실패: provider 정보가 누락되었습니다.",
                "소셜 프로필 정보 로드 중 서버 오류가 발생했습니다."),

        USER_PROFILE_PROVIDER_ID_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "SocialUserProfile 생성 실패: providerId 정보가 누락되었습니다.",
                "소셜 프로필 정보 로드 중 서버 오류가 발생했습니다."),

        USER_PROFILE_EMAIL_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "SocialUserProfile 생성 실패: email 정보가 누락되었습니다.",
                "소셜 프로필 정보 로드 중 서버 오류가 발생했습니다."),

        USER_PROFILE_NICKNAME_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "SocialUserProfile 생성 실패: nickname 정보가 누락되었습니다.",
                "소셜 프로필 정보 로드 중 서버 오류가 발생했습니다."),

        USER_PROFILE_CONNECTED_AT_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "SocialUserProfile 생성 실패: connectedAt 정보가 누락되었습니다.",
                "소셜 프로필 정보 로드 중 서버 오류가 발생했습니다."),

        USER_PROFILE_CONSENT_INFO_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "SocialUserProfile 생성 실패: 필수 동의 항목 정보가 누락되었습니다.",
                "소셜 프로필 정보 로드 중 서버 오류가 발생했습니다.");

        private final HttpStatus httpStatus;
        private final String logMessage;
        private final String clientMessage;

        @Override
        public String getErrorCode() {
            return this.name();
        }
    }
}

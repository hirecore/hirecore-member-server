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

        // VISIBILITY
        VISIBILITY_INVALID(
                HttpStatus.BAD_REQUEST,
                "Visibility 변환 실패: 지원하지 않는 visibility 값입니다.",
                "지원하지 않는 공개 범위입니다."),

        // COLLABORATION_TYPE
        COLLABORATION_TYPE_INVALID(
                HttpStatus.BAD_REQUEST,
                "CollaborationType 변환 실패: 지원하지 않는 collaborationType 값입니다.",
                "지원하지 않는 협업 유형입니다."),

        // EXTERNAL_LINK
        EXTERNAL_LINK_PAIR_INCOMPLETE(
                HttpStatus.BAD_REQUEST,
                "ExternalLink vo 객체 생성에서 label과 url이 쌍으로 제공되지 않았습니다.",
                "외부 링크의 이름과 URL을 모두 입력해주세요."),

        EXTERNAL_URL_INVALID(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "External vo 객체 생성에서 externalUrl 필드가 유효하지 않습니다.",
                "유효하지 않은 외부 링크 URL입니다. 올바른 형식으로 입력해주세요."),

        AUDITING_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "도메인 객체 생성 시 audtingInfo 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // IMAGE_UPLOADED_EVENT
        IMAGE_UPLOADED_EVENT_IMAGE_FILE_META_ID_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ImageUploadedEvent 생성에서 imageFileMetaId가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        IMAGE_UPLOADED_EVENT_MEMBER_ACCOUNT_ID_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ImageUploadedEvent 생성에서 memberAccountId가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        IMAGE_UPLOADED_EVENT_FILE_SIZE_BYTES_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ImageUploadedEvent 생성에서 fileSizeBytes가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        IMAGE_UPLOADED_EVENT_COMPLETED_UPLOAD_AT_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ImageUploadedEvent 생성에서 completedUploadAt이 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // DOMAIN_TYPE
        DOMAIN_TYPE_INVALID(
                HttpStatus.BAD_REQUEST,
                "DomainType 변환 실패: 지원하지 않는 domainType 값입니다.",
                "지원하지 않는 도메인 타입입니다."),

        // PURPOSE
        PURPOSE_INVALID(
                HttpStatus.BAD_REQUEST,
                "Purpose 변환 실패: 지원하지 않는 purpose 값입니다.",
                "지원하지 않는 파일 업로드 목적입니다."),
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

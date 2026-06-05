package io.hirecore.hirecorememberserver.modules.coverletter.domain.exception;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.DomainExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

public class CoverLetterDomainExceptionCodeCluster {
    @Getter
    @RequiredArgsConstructor
    public enum HiddenDetailResponse implements DomainExceptionCode {
        // ID
        ID_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "CoverLetter 도메인 객체 생성에서 id 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // MEMBER_ACCOUNT_ID
        MEMBER_ACCOUNT_ID_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "CoverLetter 도메인 객체 생성에서 memberAccountId 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // COVER_LETTER_JOB_CATEGORY
        COVER_LETTER_JOB_CATEGORY_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "CoverLetter 도메인 객체 생성에서 coverLetterJobCategory 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // COVER_LETTER_CONTENT
        COVER_LETTER_CONTENT_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "CoverLetter 도메인 객체 생성에서 coverLetterContent 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // TITLE
        TITLE_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "CoverLetter 도메인 객체 생성에서 title 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        TITLE_TOO_LONG(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "CoverLetter 도메인 객체 생성에서 title 길이가 허용 한도를 초과했습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // PREVIEW_SUMMARY
        PREVIEW_SUMMARY_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "CoverLetter 도메인 객체 생성에서 previewSummary 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        PREVIEW_SUMMARY_TOO_LONG(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "CoverLetter 도메인 객체 생성에서 previewSummary 길이가 허용 한도를 초과했습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // PRIVATE_MEMO
        PRIVATE_MEMO_TOO_LONG(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "CoverLetter 도메인 객체 생성에서 privateMemo 길이가 허용 한도를 초과했습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // EXTERNAL_LINKS
        EXTERNAL_LINKS_TOO_MANY(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "CoverLetter 도메인 객체 생성에서 externalLinks 개수가 허용 한도를 초과했습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // COVER_LETTER_TAGS
        COVER_LETTER_TAGS_TOO_MANY(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "CoverLetter 도메인 객체 생성에서 coverLetterTags 개수가 허용 한도를 초과했습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // STATUS
        STATUS_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "CoverLetter 도메인 객체 생성에서 status 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // VISIBILITY
        VISIBILITY_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "CoverLetter 도메인 객체 생성에서 visibility 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // COLLABORATION_TYPE
        COLLABORATION_TYPE_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "CoverLetter 도메인 객체 생성에서 collaborationType 필드가 누락되었습니다.",
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

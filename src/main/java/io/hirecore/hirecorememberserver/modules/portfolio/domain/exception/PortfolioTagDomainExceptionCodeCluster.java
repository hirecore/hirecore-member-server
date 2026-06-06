package io.hirecore.hirecorememberserver.modules.portfolio.domain.exception;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.DomainExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

public class PortfolioTagDomainExceptionCodeCluster {
    @Getter
    @RequiredArgsConstructor
    public enum HiddenDetailResponse implements DomainExceptionCode {
        // ID
        ID_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "PortfolioTag 도메인 객체 생성에서 id 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // NAME
        NAME_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "PortfolioTag 도메인 객체 생성에서 name 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // NORMALIZED_TAG
        NORMALIZED_TAG_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "PortfolioTag 도메인 객체 생성에서 normalizedTag 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        // SORT_ORDER
        SORT_ORDER_MISSING(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "PortfolioTag 도메인 객체 생성에서 sortOrder 필드가 누락되었습니다.",
                "서버에서 오류가 발생했습니다. 관리자에게 문의해주세요."),

        SORT_ORDER_NEGATIVE(
                HttpStatus.BAD_REQUEST,
                "PortfolioTag 도메인 객체 생성에서 sortOrder 값이 음수입니다.",
                "태그 정렬 순서는 0 이상이어야 합니다."),

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

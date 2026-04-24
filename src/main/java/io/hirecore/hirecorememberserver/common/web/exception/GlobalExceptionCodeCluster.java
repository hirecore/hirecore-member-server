package io.hirecore.hirecorememberserver.common.web.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 전역 공통 에러 코드 클러스터
 * <p>
 * 발생 주체와 책임 소재에 따라 {@link Interface}와 {@link Server}로 구분합니다.
 * 별도의 code 필드 없이 Enum 상수의 이름(name)을 에러 코드로 사용합니다.
 */
public class GlobalExceptionCodeCluster {

    @Getter
    @RequiredArgsConstructor
    public enum Interface {
        // [400] Validation (입력값 검증 실패)
        REQUEST_VALUE_INVALID(HttpStatus.BAD_REQUEST, "입력된 요청값에 오류가 있습니다."),
        REQUEST_CONSTRAINT_VIOLATED(HttpStatus.BAD_REQUEST, "요청 데이터가 유효성 제약 조건을 위반했습니다."),

        // [400] JSON Parsing & Format (JSON 문법 및 매핑 오류)
        JSON_FORMAT_INVALID(HttpStatus.BAD_REQUEST, "요청 본문(Body)을 읽을 수 없습니다. JSON 형식을 확인해주세요."),
        JSON_GRAMMAR_INVALID(HttpStatus.BAD_REQUEST, "전송된 JSON의 문법이 올바르지 않습니다. (괄호, 콤마 등 확인)"),
        JSON_TYPE_INVALID(HttpStatus.BAD_REQUEST, "입력된 값의 형식이 올바르지 않습니다. ('%s' 타입이어야 합니다.)"),
        JSON_STRUCTURE_INVALID(HttpStatus.BAD_REQUEST, "JSON 객체 또는 배열 구조가 올바르지 않습니다."),
        JSON_PROPERTY_UNKNOWN(HttpStatus.BAD_REQUEST, "알 수 없는 필드가 포함되어 있습니다."),

        // [404] Resource Not Found (경로 오류)
        RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청하신 API 경로(URI) 또는 리소스를 찾을 수 없습니다.");

        private final HttpStatus httpStatus;
        private final String message;

        public String getCode() {
            return this.name();
        }
    }

    @Getter
    @RequiredArgsConstructor
    public enum Security {
        AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "인증에 실패하였습니다."),
        ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 리소스에 접근할 권한이 없습니다.");

        private final HttpStatus httpStatus;
        private final String message;

        public String getCode() {
            return this.name();
        }
    }

    @Getter
    @RequiredArgsConstructor
    public enum Server {
        // [409] Data Integrity Violation (DB 유니크 제약 등)
        SERVER_DATA_INTEGRITY_VIOLATION(HttpStatus.CONFLICT, "이미 존재하는 데이터입니다."),

        // [500] Critical Data Issue (논리적 데이터 오류)
        SERVER_DATA_INCONSISTENT(HttpStatus.INTERNAL_SERVER_ERROR, "데이터 처리 중 논리적 정합성 오류가 발생했습니다."),

        // [500] Unhandled Exception (서버 내부 오류)
        SERVER_INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "일시적인 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");

        private final HttpStatus httpStatus;
        private final String message;

        public String getCode() {
            return this.name();
        }
    }
}

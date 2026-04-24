package io.hirecore.hirecorememberserver.common.web.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class ErrorResponse {

    @Builder.Default
    private final Instant timestamp = Instant.now();
    private final String trackingId;
    private final String errorCode;
    private final String message;
    private final String path;

    /**
     * [변경] errors -> fieldErrors
     * Validation(@Valid) 실패 시, 실패한 필드와 이유 목록을 담습니다.
     * 명칭을 구체화하여 일반 에러 메시지와 혼동을 방지했습니다.
     */
    private final List<FieldErrorDetail> fieldErrors;

    /**
     * [변경] data -> attributes
     * 에러 발생 시 클라이언트가 참고할 수 있는 부가 데이터입니다.
     * (성공 응답의 'data' 필드와 구분하기 위해 이름을 변경했습니다.)
     */
    private final Map<String, Object> attributes;

    @Getter
    @Builder
    public static class FieldErrorDetail {
        private final String field;
        private final String reason;
    }
}

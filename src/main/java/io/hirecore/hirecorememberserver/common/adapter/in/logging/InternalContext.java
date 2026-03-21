package io.hirecore.hirecorememberserver.common.adapter.in.logging;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

/**
 * 서버 내부 전용 진단 정보.
 * 클라이언트 응답(res)에는 절대 포함되지 않으며, 로그 파일에만 기록됩니다.
 */
@Getter
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InternalContext {

    /** 발생한 예외 클래스명 */
    private final String exceptionName;

    /** 예외를 처리한 핸들러 메서드명 */
    private final String handler;

    /**
     * 도메인 예외 코드의 서버 전용 메시지 (DomainExceptionCode.logMessage).
     * HiddenDetailResponse 계열에서만 clientMessage와 다른 값을 가지며,
     * 클라이언트에게는 절대 노출되지 않습니다.
     */
    private final String logMessage;

    /** 핸들러에서 추가한 디버그 메타데이터 */
    private final Map<String, Object> debugMetadata;
}

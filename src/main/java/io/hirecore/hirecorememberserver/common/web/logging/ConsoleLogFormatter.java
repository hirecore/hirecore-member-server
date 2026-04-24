package io.hirecore.hirecorememberserver.common.web.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.hirecore.hirecorememberserver.common.web.exception.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ConsoleLogFormatter {

    private final ObjectMapper objectMapper;

    /**
     * 로그 파일과 동일한 구조(JSON)를 유지하되, 콘솔 가독성을 위해 Pretty Print를 적용합니다.
     * MDC 정보(traceId 등)도 포함하여 출력합니다.
     */
    public String formatPrettyJson(ErrorLogContext reqCtx, InternalContext internalCtx, ErrorResponse res) {
        Map<String, Object> logMap = new LinkedHashMap<>();

        Map<String, String> mdcMap = MDC.getCopyOfContextMap();
        if (mdcMap != null) {
            logMap.putAll(mdcMap);
        }

        logMap.put("errorCode", res.getErrorCode());
        logMap.put("req", reqCtx);
        logMap.put("res", res);
        logMap.put("internal", internalCtx);

        try {
            return objectMapper.enable(SerializationFeature.INDENT_OUTPUT)
                    .writeValueAsString(logMap);
        } catch (JsonProcessingException e) {
            return "LOG FORMAT ERROR: " + e.getMessage();
        }
    }
}

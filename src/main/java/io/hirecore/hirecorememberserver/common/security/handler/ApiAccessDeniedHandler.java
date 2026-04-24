package io.hirecore.hirecorememberserver.common.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hirecore.hirecorememberserver.common.web.exception.ErrorResponse;
import io.hirecore.hirecorememberserver.common.web.exception.GlobalExceptionCodeCluster;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * 인증된 사용자(Authenticated)가 권한이 부족하여 리소스에 접근할 수 없을 때 처리하는 핸들러입니다.
 * <p>
 * Spring Security 필터 체인(FilterSecurityInterceptor)에서 AccessDeniedException이 발생했을 때 호출되며,
 * 클라이언트에게 통일된 {@link ErrorResponse} 포맷으로 403 Forbidden 응답을 반환합니다.
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApiAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;
    private static final String TRACE_ID_KEY = "traceId";

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {

        log.warn("Access Denied: {}", accessDeniedException.getMessage());

        GlobalExceptionCodeCluster.Security errorCode = GlobalExceptionCodeCluster.Security.ACCESS_DENIED;

        ErrorResponse errorResponse = ErrorResponse.builder()
                .trackingId(ensureTrackingId())
                .errorCode(errorCode.getCode())
                .message(errorCode.getMessage())
                .path(request.getRequestURI())
                .build();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(errorCode.getHttpStatus().value());

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }

    private String ensureTrackingId() {
        String traceId = MDC.get(TRACE_ID_KEY);
        if (traceId == null || traceId.isEmpty()) {
            traceId = UUID.randomUUID().toString().substring(0, 8);
            MDC.put(TRACE_ID_KEY, traceId);
        }
        return traceId;
    }
}

package io.hirecore.hirecorememberserver.common.adapter.in.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hirecore.hirecorememberserver.common.exception.ErrorResponse;
import io.hirecore.hirecorememberserver.common.exception.GlobalExceptionCodeCluster;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * 인증되지 않은 사용자(Unauthenticated)의 요청을 처리하는 핸들러입니다.
 * <p>
 * Spring Security 필터 체인 내에서 인증 예외가 발생하거나 인증 정보가 없을 때 호출되며,
 * 클라이언트에게 통일된 {@link ErrorResponse} 포맷으로 401 Unauthorized 응답을 반환합니다.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    private static final String TRACE_ID_KEY = "traceId";

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        GlobalExceptionCodeCluster.Security errorCode = GlobalExceptionCodeCluster.Security.AUTHENTICATION_FAILED;

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

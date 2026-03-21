package io.hirecore.hirecorememberserver.common.adapter.in.logging;

import io.hirecore.hirecorememberserver.common.domain.exception.BaseDomainException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Component
public class LogContextFactory {

    public ErrorLogContext createReqContext(HttpServletRequest request) {
        return ErrorLogContext.builder()
                .httpMethod(request.getMethod())
                .requestUri(request.getRequestURI())
                .queryString(request.getQueryString() == null ? "none" : request.getQueryString())
                .clientIp(getClientIp(request))
                .userAgent(request.getHeader("User-Agent"))
                .userPrincipal(request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "anonymous")
                .build();
    }

    public InternalContext createInternalContext(Exception e, Map<String, Object> debugMetadata) {
        String logMessage = (e instanceof BaseDomainException domainEx) ? domainEx.getLogMessage() : null;

        return InternalContext.builder()
                .exceptionName(e.getClass().getSimpleName())
                .handler(getHandlerMethodName())
                .logMessage(logMessage)
                .debugMetadata(debugMetadata)
                .build();
    }

    private String getHandlerMethodName() {
        return StackWalker.getInstance()
                .walk(frames -> frames.skip(3)
                        .findFirst()
                        .map(StackWalker.StackFrame::getMethodName)
                        .orElse("unknown"));
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) ip = request.getRemoteAddr();
        return (StringUtils.hasText(ip) && ip.contains(",")) ? ip.split(",")[0].trim() : ip;
    }
}

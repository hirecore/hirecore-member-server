package io.hirecore.hirecorememberserver.common.web.logging;

import io.hirecore.hirecorememberserver.common.web.exception.ErrorResponse;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.BaseDomainException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Component
@RequiredArgsConstructor
public class StructuredErrorLogger {

    private final LogContextFactory contextFactory;
    private final ConsoleLogFormatter logFormatter;

    public void log(
            HttpServletRequest request,
            Exception e,
            ErrorResponse response,
            Map<String, Object> debugMetadata
    ) {
        ErrorLogContext reqContext = contextFactory.createReqContext(request);
        InternalContext internalContext = contextFactory.createInternalContext(e, debugMetadata);
        boolean isError = isErrorLevel(response, e);

        String prettyJsonMessage = logFormatter.formatPrettyJson(reqContext, internalContext, response);

        if (isError) {
            log.error(prettyJsonMessage,
                    kv("errorCode", response.getErrorCode()),
                    kv("req", reqContext),
                    kv("internal", internalContext),
                    kv("res", response),
                    e
            );
        } else {
            log.warn(prettyJsonMessage,
                    kv("errorCode", response.getErrorCode()),
                    kv("req", reqContext),
                    kv("internal", internalContext),
                    kv("res", response)
            );
        }
    }

    private boolean isErrorLevel(ErrorResponse res, Exception e) {
        if (e instanceof BaseDomainException dex) return dex.getHttpStatus().is5xxServerError();
        return res.getErrorCode().startsWith("5") || res.getErrorCode().contains("INTERNAL");
    }
}

package io.hirecore.hirecorememberserver.common.application.exception;

import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.Map;

public abstract class BaseApplicationException extends RuntimeException implements ApplicationException {

    private final ApplicationExceptionCode applicationExceptionCode;
    private final String exceptionCategory;
    private final Map<String, Object> context;

    protected BaseApplicationException(
            String exceptionCategory,
            ApplicationExceptionCode applicationExceptionCode
    ) {
        this(exceptionCategory, applicationExceptionCode, Collections.emptyMap(), null);
    }

    protected BaseApplicationException(
            String exceptionCategory,
            ApplicationExceptionCode applicationExceptionCode,
            Map<String, Object> context,
            Throwable cause
    ) {
        super(applicationExceptionCode.getClientMessage(), cause);

        this.exceptionCategory = exceptionCategory;
        this.applicationExceptionCode = applicationExceptionCode;
        this.context = context;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return applicationExceptionCode.getHttpStatus();
    }

    @Override
    public String getErrorCode() {
        return applicationExceptionCode.getErrorCode();
    }

    @Override
    public String getLogMessage() {
        return applicationExceptionCode.getLogMessage();
    }

    @Override
    public String getClientMessage() {
        return applicationExceptionCode.getClientMessage();
    }

    @Override
    public String getExceptionCategory() {
        return this.exceptionCategory;
    }

    public Map<String, Object> getContext() {
        return this.context;
    }
}

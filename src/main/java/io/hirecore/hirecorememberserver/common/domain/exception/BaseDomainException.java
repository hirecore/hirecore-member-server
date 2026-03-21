package io.hirecore.hirecorememberserver.common.domain.exception;

import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.Map;

public abstract class BaseDomainException extends RuntimeException implements DomainException {

    private final DomainExceptionCode domainExceptionCode;
    private final String exceptionCategory;
    private final Map<String, Object> context;

    protected BaseDomainException(
            String exceptionCategory,
            DomainExceptionCode domainExceptionCode
    ) {
        this(exceptionCategory, domainExceptionCode, Collections.emptyMap(), null);
    }

    protected BaseDomainException(
            String exceptionCategory,
            DomainExceptionCode domainExceptionCode,
            Map<String, Object> context
    ) {
        this(exceptionCategory, domainExceptionCode, context, null);
    }

    protected BaseDomainException(
            String exceptionCategory,
            DomainExceptionCode domainExceptionCode,
            Map<String, Object> context,
            Throwable cause
    ) {
        super(domainExceptionCode.getClientMessage(), cause);

        this.exceptionCategory = exceptionCategory;
        this.domainExceptionCode = domainExceptionCode;
        this.context = context;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return domainExceptionCode.getHttpStatus();
    }

    @Override
    public String getErrorCode() {
        return domainExceptionCode.getErrorCode();
    }

    @Override
    public String getLogMessage() {
        return domainExceptionCode.getLogMessage();
    }

    @Override
    public String getClientMessage() {
        return domainExceptionCode.getClientMessage();
    }

    @Override
    public String getExceptionCategory() {
        return this.exceptionCategory;
    }

    public Map<String, Object> getContext() {
        return this.context;
    }
}

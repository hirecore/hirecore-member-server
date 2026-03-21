package io.hirecore.hirecorememberserver.common.domain.exception;

import org.springframework.http.HttpStatus;

public interface DomainException {
    String getExceptionCategory();
    HttpStatus getHttpStatus();
    String getErrorCode();
    String getLogMessage();
    String getClientMessage();
}

package io.hirecore.hirecorememberserver.sharedkernel.domain.exception;

import org.springframework.http.HttpStatus;

public interface DomainException {
    String getExceptionCategory();
    HttpStatus getHttpStatus();
    String getErrorCode();
    String getLogMessage();
    String getClientMessage();
}

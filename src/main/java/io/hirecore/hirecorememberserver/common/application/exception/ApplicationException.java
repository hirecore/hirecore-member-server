package io.hirecore.hirecorememberserver.common.application.exception;

import org.springframework.http.HttpStatus;

public interface ApplicationException {
    String getExceptionCategory();
    HttpStatus getHttpStatus();
    String getErrorCode();
    String getLogMessage();
    String getClientMessage();
}

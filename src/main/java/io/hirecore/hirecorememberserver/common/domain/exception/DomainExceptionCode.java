package io.hirecore.hirecorememberserver.common.domain.exception;

import org.springframework.http.HttpStatus;

public interface DomainExceptionCode {
    HttpStatus getHttpStatus();
    String getErrorCode();
    String getLogMessage();
    String getClientMessage();
}

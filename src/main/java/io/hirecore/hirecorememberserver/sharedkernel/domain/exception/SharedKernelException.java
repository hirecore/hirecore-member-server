package io.hirecore.hirecorememberserver.sharedkernel.domain.exception;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.BaseDomainException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.DomainExceptionCode;

import java.util.Map;

public class SharedKernelException extends BaseDomainException {
    public SharedKernelException(DomainExceptionCode domainExceptionCode) {
        super("SharedKernelException", domainExceptionCode);
    }

    public SharedKernelException(DomainExceptionCode domainExceptionCode, Map<String, Object> context) {
        super("SharedKernelException", domainExceptionCode, context);
    }
}

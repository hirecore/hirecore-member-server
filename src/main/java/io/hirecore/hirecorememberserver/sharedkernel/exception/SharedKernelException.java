package io.hirecore.hirecorememberserver.sharedkernel.exception;

import io.hirecore.hirecorememberserver.common.domain.exception.BaseDomainException;
import io.hirecore.hirecorememberserver.common.domain.exception.DomainExceptionCode;

import java.util.Map;

public class SharedKernelException extends BaseDomainException {
    public SharedKernelException(DomainExceptionCode domainExceptionCode) {
        super("SharedKernelException", domainExceptionCode);
    }

    public SharedKernelException(DomainExceptionCode domainExceptionCode, Map<String, Object> context) {
        super("SharedKernelException", domainExceptionCode, context);
    }
}

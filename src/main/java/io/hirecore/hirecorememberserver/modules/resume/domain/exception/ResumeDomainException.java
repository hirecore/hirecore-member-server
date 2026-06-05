package io.hirecore.hirecorememberserver.modules.resume.domain.exception;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.BaseDomainException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.DomainExceptionCode;

public class ResumeDomainException extends BaseDomainException {
    public ResumeDomainException(DomainExceptionCode domainExceptionCode) {
        super("ResumeDomainException", domainExceptionCode);
    }
}

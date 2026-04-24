package io.hirecore.hirecorememberserver.modules.resume.domain.exception;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.BaseDomainException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.DomainExceptionCode;

public class ResumeTagDomainException extends BaseDomainException {
    public ResumeTagDomainException(DomainExceptionCode domainExceptionCode) {
        super("ResumeTagDomainException", domainExceptionCode);
    }
}

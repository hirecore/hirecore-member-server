package io.hirecore.hirecorememberserver.modules.resume.domain.exception;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.BaseDomainException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.DomainExceptionCode;

public class ResumeContentDomainException extends BaseDomainException {
    public ResumeContentDomainException(DomainExceptionCode domainExceptionCode) {
        super("ResumeContentDomainException", domainExceptionCode);
    }
}

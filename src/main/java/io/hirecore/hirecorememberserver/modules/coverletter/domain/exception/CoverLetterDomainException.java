package io.hirecore.hirecorememberserver.modules.coverletter.domain.exception;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.BaseDomainException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.DomainExceptionCode;

public class CoverLetterDomainException extends BaseDomainException {
    public CoverLetterDomainException(DomainExceptionCode domainExceptionCode) {
        super("CoverLetterDomainException", domainExceptionCode);
    }
}

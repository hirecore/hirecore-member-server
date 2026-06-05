package io.hirecore.hirecorememberserver.modules.coverletter.domain.exception;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.BaseDomainException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.DomainExceptionCode;

public class CoverLetterContentDomainException extends BaseDomainException {
    public CoverLetterContentDomainException(DomainExceptionCode domainExceptionCode) {
        super("CoverLetterContentDomainException", domainExceptionCode);
    }
}

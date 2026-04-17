package io.hirecore.hirecorememberserver.modules.coverletter.domain.exception;

import io.hirecore.hirecorememberserver.common.domain.exception.BaseDomainException;
import io.hirecore.hirecorememberserver.common.domain.exception.DomainExceptionCode;

public class CoverLetterTagDomainException extends BaseDomainException {
    public CoverLetterTagDomainException(DomainExceptionCode domainExceptionCode) {
        super("CoverLetterTagDomainException", domainExceptionCode);
    }
}

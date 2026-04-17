package io.hirecore.hirecorememberserver.modules.coverletter.domain.exception;

import io.hirecore.hirecorememberserver.common.domain.exception.BaseDomainException;
import io.hirecore.hirecorememberserver.common.domain.exception.DomainExceptionCode;

public class CoverLetterJobCategoryDomainException extends BaseDomainException {
    public CoverLetterJobCategoryDomainException(DomainExceptionCode domainExceptionCode) {
        super("CoverLetterJobCategoryDomainException", domainExceptionCode);
    }
}

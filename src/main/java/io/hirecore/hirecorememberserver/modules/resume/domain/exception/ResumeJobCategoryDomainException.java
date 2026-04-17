package io.hirecore.hirecorememberserver.modules.resume.domain.exception;

import io.hirecore.hirecorememberserver.common.domain.exception.BaseDomainException;
import io.hirecore.hirecorememberserver.common.domain.exception.DomainExceptionCode;

public class ResumeJobCategoryDomainException extends BaseDomainException {
    public ResumeJobCategoryDomainException(DomainExceptionCode domainExceptionCode) {
        super("ResumeJobCategoryDomainException", domainExceptionCode);
    }
}

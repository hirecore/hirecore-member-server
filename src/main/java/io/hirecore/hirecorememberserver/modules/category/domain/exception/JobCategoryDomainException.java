package io.hirecore.hirecorememberserver.modules.category.domain.exception;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.BaseDomainException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.DomainExceptionCode;

public class JobCategoryDomainException extends BaseDomainException {
    public JobCategoryDomainException(DomainExceptionCode domainExceptionCode) {
        super("JobCategoryDomainException", domainExceptionCode);
    }
}

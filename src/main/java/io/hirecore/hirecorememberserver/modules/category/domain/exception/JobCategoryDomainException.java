package io.hirecore.hirecorememberserver.modules.category.domain.exception;

import io.hirecore.hirecorememberserver.common.domain.exception.BaseDomainException;
import io.hirecore.hirecorememberserver.common.domain.exception.DomainExceptionCode;

public class JobCategoryDomainException extends BaseDomainException {
    public JobCategoryDomainException(DomainExceptionCode domainExceptionCode) {
        super("JobCategoryDomainException", domainExceptionCode);
    }
}

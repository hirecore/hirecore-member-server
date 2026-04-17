package io.hirecore.hirecorememberserver.modules.portfolio.domain.exception;

import io.hirecore.hirecorememberserver.common.domain.exception.BaseDomainException;
import io.hirecore.hirecorememberserver.common.domain.exception.DomainExceptionCode;

public class PortfolioJobCategoryDomainException extends BaseDomainException {
    public PortfolioJobCategoryDomainException(DomainExceptionCode domainExceptionCode) {
        super("PortfolioJobCategoryDomainException", domainExceptionCode);
    }
}

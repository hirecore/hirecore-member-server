package io.hirecore.hirecorememberserver.modules.portfolio.domain.exception;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.BaseDomainException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.DomainExceptionCode;

public class PortfolioInterestDomainException extends BaseDomainException {
    public PortfolioInterestDomainException(DomainExceptionCode domainExceptionCode) {
        super("PortfolioInterestDomainException", domainExceptionCode);
    }
}

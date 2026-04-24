package io.hirecore.hirecorememberserver.modules.portfolio.domain.exception;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.BaseDomainException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.DomainExceptionCode;

public class PortfolioContentDomainException extends BaseDomainException {
    public PortfolioContentDomainException(DomainExceptionCode domainExceptionCode) {
        super("PortfolioContentDomainException", domainExceptionCode);
    }
}

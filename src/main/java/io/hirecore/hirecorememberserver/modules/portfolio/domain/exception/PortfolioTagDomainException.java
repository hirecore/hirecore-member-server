package io.hirecore.hirecorememberserver.modules.portfolio.domain.exception;

import io.hirecore.hirecorememberserver.common.domain.exception.BaseDomainException;
import io.hirecore.hirecorememberserver.common.domain.exception.DomainExceptionCode;

public class PortfolioTagDomainException extends BaseDomainException {
    public PortfolioTagDomainException(DomainExceptionCode domainExceptionCode) {
        super("PortfolioTagDomainException", domainExceptionCode);
    }
}

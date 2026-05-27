package io.hirecore.hirecorememberserver.modules.portfolio.domain.exception;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.BaseDomainException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.DomainExceptionCode;

public class PortfolioMemberInterestDomainException extends BaseDomainException {
    public PortfolioMemberInterestDomainException(DomainExceptionCode domainExceptionCode) {
        super("PortfolioMemberInterestDomainException", domainExceptionCode);
    }
}

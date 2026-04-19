package io.hirecore.hirecorememberserver.modules.portfolio.domain.exception;

import io.hirecore.hirecorememberserver.common.domain.exception.BaseDomainException;
import io.hirecore.hirecorememberserver.common.domain.exception.DomainExceptionCode;

public class PortfolioMemberViewDomainException extends BaseDomainException {
    public PortfolioMemberViewDomainException(DomainExceptionCode domainExceptionCode) {
        super("PortfolioMemberViewDomainException", domainExceptionCode);
    }
}

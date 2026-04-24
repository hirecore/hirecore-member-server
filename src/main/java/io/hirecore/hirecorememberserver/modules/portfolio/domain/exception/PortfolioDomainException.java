package io.hirecore.hirecorememberserver.modules.portfolio.domain.exception;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.BaseDomainException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.DomainExceptionCode;

public class PortfolioDomainException extends BaseDomainException {
    public PortfolioDomainException(DomainExceptionCode domainExceptionCode) {
        super("PortfolioDomainException", domainExceptionCode);
    }
}

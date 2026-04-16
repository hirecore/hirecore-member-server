package io.hirecore.hirecorememberserver.modules.portfolio.domain.exception;

import io.hirecore.hirecorememberserver.common.domain.exception.BaseDomainException;
import io.hirecore.hirecorememberserver.common.domain.exception.DomainExceptionCode;

public class PortfolioDomainException extends BaseDomainException {
    public PortfolioDomainException(DomainExceptionCode domainExceptionCode) {
        super("PortfolioDomainException", domainExceptionCode);
    }
}

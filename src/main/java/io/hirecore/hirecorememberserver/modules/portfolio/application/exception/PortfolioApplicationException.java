package io.hirecore.hirecorememberserver.modules.portfolio.application.exception;

import io.hirecore.hirecorememberserver.sharedkernel.application.exception.ApplicationExceptionCode;
import io.hirecore.hirecorememberserver.sharedkernel.application.exception.BaseApplicationException;

public class PortfolioApplicationException extends BaseApplicationException {
    public PortfolioApplicationException(ApplicationExceptionCode applicationExceptionCode) {
        super("PortfolioApplicationException", applicationExceptionCode);
    }
}

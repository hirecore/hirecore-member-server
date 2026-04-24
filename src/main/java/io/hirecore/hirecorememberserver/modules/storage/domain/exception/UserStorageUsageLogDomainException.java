package io.hirecore.hirecorememberserver.modules.storage.domain.exception;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.BaseDomainException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.DomainExceptionCode;

public class UserStorageUsageLogDomainException extends BaseDomainException {
    public UserStorageUsageLogDomainException(DomainExceptionCode domainExceptionCode) {
        super("UserStorageUsageLogDomainException", domainExceptionCode);
    }
}

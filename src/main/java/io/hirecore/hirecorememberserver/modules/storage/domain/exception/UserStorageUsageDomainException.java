package io.hirecore.hirecorememberserver.modules.storage.domain.exception;

import io.hirecore.hirecorememberserver.common.domain.exception.BaseDomainException;
import io.hirecore.hirecorememberserver.common.domain.exception.DomainExceptionCode;

public class UserStorageUsageDomainException extends BaseDomainException {
    public UserStorageUsageDomainException(DomainExceptionCode domainExceptionCode) {
        super("UserStorageUsageDomainException", domainExceptionCode);
    }
}

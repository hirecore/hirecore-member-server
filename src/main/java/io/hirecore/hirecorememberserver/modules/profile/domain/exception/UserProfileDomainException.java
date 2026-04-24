package io.hirecore.hirecorememberserver.modules.profile.domain.exception;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.BaseDomainException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.DomainExceptionCode;

public class UserProfileDomainException extends BaseDomainException {
    public UserProfileDomainException(DomainExceptionCode domainExceptionCode) {
        super("UserProfileDomainException", domainExceptionCode);
    }
}

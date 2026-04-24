package io.hirecore.hirecorememberserver.modules.profile.domain.exception;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.BaseDomainException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.DomainExceptionCode;

public class ProfileDomainException extends BaseDomainException {
    public ProfileDomainException(DomainExceptionCode domainExceptionCode) {
        super("ProfileDomainException", domainExceptionCode);
    }
}

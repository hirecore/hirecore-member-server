package io.hirecore.hirecorememberserver.modules.profile.domain.exception;

import io.hirecore.hirecorememberserver.common.domain.exception.BaseDomainException;
import io.hirecore.hirecorememberserver.common.domain.exception.DomainExceptionCode;

public class ProfileDomainException extends BaseDomainException {
    public ProfileDomainException(DomainExceptionCode domainExceptionCode) {
        super("ProfileDomainException", domainExceptionCode);
    }
}

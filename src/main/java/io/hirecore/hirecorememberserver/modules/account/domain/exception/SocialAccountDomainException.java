package io.hirecore.hirecorememberserver.modules.account.domain.exception;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.BaseDomainException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.DomainExceptionCode;

public class SocialAccountDomainException extends BaseDomainException {
    public SocialAccountDomainException(DomainExceptionCode domainExceptionCode) {
        super("SocialAccountDomainException", domainExceptionCode);
    }
}

package io.hirecore.hirecorememberserver.modules.account.domain.exception;

import io.hirecore.hirecorememberserver.common.domain.exception.BaseDomainException;
import io.hirecore.hirecorememberserver.common.domain.exception.DomainExceptionCode;

public class SocialAccountDomainException extends BaseDomainException {
    public SocialAccountDomainException(DomainExceptionCode domainExceptionCode) {
        super("SocialAccountDomainException", domainExceptionCode);
    }
}

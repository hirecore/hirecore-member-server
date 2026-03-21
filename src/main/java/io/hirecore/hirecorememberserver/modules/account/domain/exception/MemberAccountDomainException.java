package io.hirecore.hirecorememberserver.modules.account.domain.exception;

import io.hirecore.hirecorememberserver.common.domain.exception.BaseDomainException;
import io.hirecore.hirecorememberserver.common.domain.exception.DomainExceptionCode;

import java.util.Map;

public class MemberAccountDomainException extends BaseDomainException {
    public MemberAccountDomainException(DomainExceptionCode domainExceptionCode) {
        super("MemberAccountDomainException", domainExceptionCode);
    }

    public MemberAccountDomainException(DomainExceptionCode domainExceptionCode, Map<String, Object> context) {
        super("MemberAccountDomainException", domainExceptionCode, context);
    }
}

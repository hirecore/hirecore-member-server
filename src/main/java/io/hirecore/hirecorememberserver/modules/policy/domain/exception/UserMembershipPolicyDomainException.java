package io.hirecore.hirecorememberserver.modules.policy.domain.exception;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.BaseDomainException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.DomainExceptionCode;

import java.util.Map;

public class UserMembershipPolicyDomainException extends BaseDomainException {
    public UserMembershipPolicyDomainException(DomainExceptionCode domainExceptionCode) {
        super("UserMembershipPolicyDomainException", domainExceptionCode);
    }

    public UserMembershipPolicyDomainException(DomainExceptionCode domainExceptionCode, Map<String, Object> context) {
        super("UserMembershipPolicyDomainException", domainExceptionCode, context);
    }
}

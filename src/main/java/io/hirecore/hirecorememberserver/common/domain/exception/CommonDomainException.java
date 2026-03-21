package io.hirecore.hirecorememberserver.common.domain.exception;

import java.util.Map;

public class CommonDomainException extends BaseDomainException {
    public CommonDomainException(DomainExceptionCode domainExceptionCode) {
        super("CommonDomainException", domainExceptionCode);
    }

    public CommonDomainException(DomainExceptionCode domainExceptionCode, Map<String, Object> context) {
        super("CommonDomainException", domainExceptionCode, context);
    }
}

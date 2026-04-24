package io.hirecore.hirecorememberserver.modules.account.application.exception;

import io.hirecore.hirecorememberserver.sharedkernel.application.exception.ApplicationExceptionCode;
import io.hirecore.hirecorememberserver.sharedkernel.application.exception.BaseApplicationException;

public class SocialAccountApplicationException extends BaseApplicationException {
    public SocialAccountApplicationException(ApplicationExceptionCode applicationExceptionCode) {
        super("SocialAccountApplicationException", applicationExceptionCode);
    }
}

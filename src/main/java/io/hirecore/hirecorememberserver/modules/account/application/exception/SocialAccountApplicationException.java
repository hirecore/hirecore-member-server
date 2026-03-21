package io.hirecore.hirecorememberserver.modules.account.application.exception;

import io.hirecore.hirecorememberserver.common.application.exception.ApplicationExceptionCode;
import io.hirecore.hirecorememberserver.common.application.exception.BaseApplicationException;

public class SocialAccountApplicationException extends BaseApplicationException {
    public SocialAccountApplicationException(ApplicationExceptionCode applicationExceptionCode) {
        super("SocialAccountApplicationException", applicationExceptionCode);
    }
}

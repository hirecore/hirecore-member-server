package io.hirecore.hirecorememberserver.modules.storage.application.exception;

import io.hirecore.hirecorememberserver.sharedkernel.application.exception.ApplicationExceptionCode;
import io.hirecore.hirecorememberserver.sharedkernel.application.exception.BaseApplicationException;

public class UserStorageApplicationException extends BaseApplicationException {
    public UserStorageApplicationException(ApplicationExceptionCode applicationExceptionCode) {
        super("UserStorageApplicationException", applicationExceptionCode);
    }
}

package io.hirecore.hirecorememberserver.modules.file.application.exception;

import io.hirecore.hirecorememberserver.common.application.exception.ApplicationExceptionCode;
import io.hirecore.hirecorememberserver.common.application.exception.BaseApplicationException;

public class FileApplicationException extends BaseApplicationException {
    public FileApplicationException(ApplicationExceptionCode applicationExceptionCode) {
        super("FileApplicationException", applicationExceptionCode);
    }
}

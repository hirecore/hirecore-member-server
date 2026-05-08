package io.hirecore.hirecorememberserver.modules.category.application.exception;

import io.hirecore.hirecorememberserver.sharedkernel.application.exception.ApplicationExceptionCode;
import io.hirecore.hirecorememberserver.sharedkernel.application.exception.BaseApplicationException;

public class CategoryApplicationException extends BaseApplicationException {
    public CategoryApplicationException(ApplicationExceptionCode applicationExceptionCode) {
        super("CategoryApplicationException", applicationExceptionCode);
    }
}

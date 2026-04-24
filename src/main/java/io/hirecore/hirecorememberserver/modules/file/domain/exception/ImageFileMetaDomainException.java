package io.hirecore.hirecorememberserver.modules.file.domain.exception;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.BaseDomainException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.DomainExceptionCode;

public class ImageFileMetaDomainException extends BaseDomainException {
    public ImageFileMetaDomainException(DomainExceptionCode domainExceptionCode) {
        super("ImageFileMetaDomainException", domainExceptionCode);
    }
}

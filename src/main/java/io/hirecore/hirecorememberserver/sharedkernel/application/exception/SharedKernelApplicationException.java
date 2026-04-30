package io.hirecore.hirecorememberserver.sharedkernel.application.exception;

public class SharedKernelApplicationException extends BaseApplicationException {
    public SharedKernelApplicationException(ApplicationExceptionCode applicationExceptionCode) {
        super("SharedKernelApplicationException", applicationExceptionCode);
    }
}

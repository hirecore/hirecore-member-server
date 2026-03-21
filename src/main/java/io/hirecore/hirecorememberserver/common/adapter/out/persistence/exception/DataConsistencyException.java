package io.hirecore.hirecorememberserver.common.adapter.out.persistence.exception;

public class DataConsistencyException extends RuntimeException {

    public DataConsistencyException(String message) {
        super(message);
    }

    public DataConsistencyException(String message, Throwable cause) {
        super(message, cause);
    }
}

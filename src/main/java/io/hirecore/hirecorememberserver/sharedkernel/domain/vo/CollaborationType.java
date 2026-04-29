package io.hirecore.hirecorememberserver.sharedkernel.domain.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelExceptionCodeCluster.HiddenDetailResponse;

public enum CollaborationType {
    TEAM("team"),
    PERSONAL("personal");

    private final String value;

    CollaborationType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static CollaborationType from(String value) {
        for (CollaborationType collaborationType : values()) {
            if (collaborationType.value.equalsIgnoreCase(value)) {
                return collaborationType;
            }
        }
        throw new SharedKernelException(HiddenDetailResponse.COLLABORATION_TYPE_INVALID);
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}

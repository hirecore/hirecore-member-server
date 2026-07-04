package io.hirecore.hirecorememberserver.sharedkernel.domain.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelExceptionCodeCluster.HiddenDetailResponse;

public enum Visibility {
    PUBLIC("public"), PRIVATE("private");

    private final String value;

    Visibility(String value) {
        this.value = value;
    }

    @JsonCreator
    public static Visibility from(String value) {
        for (Visibility visibility : values()) {
            if (visibility.value.equalsIgnoreCase(value)) {
                return visibility;
            }
        }
        throw new SharedKernelException(HiddenDetailResponse.VISIBILITY_INVALID);
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * 공개(PUBLIC) 가시성인지 여부를 반환합니다.
     */
    public boolean isPublic() {
        return this == PUBLIC;
    }
}

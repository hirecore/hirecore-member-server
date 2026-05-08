package io.hirecore.hirecorememberserver.sharedkernel.domain.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelExceptionCodeCluster.HiddenDetailResponse;

public enum DomainType {
    PORTFOLIO("portfolio"),
    RESUME("resume");

    private final String value;

    DomainType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static DomainType from(String value) {
        for (DomainType domainType : values()) {
            if (domainType.value.equalsIgnoreCase(value)) {
                return domainType;
            }
        }
        throw new SharedKernelException(HiddenDetailResponse.DOMAIN_TYPE_INVALID);
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}

package io.hirecore.hirecorememberserver.modules.portfolio.domain.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioDomainException;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioDomainExceptionCodeCluster;

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
        throw new PortfolioDomainException(
                PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.UNSUPPORTED_VISIBILITY
        );
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}

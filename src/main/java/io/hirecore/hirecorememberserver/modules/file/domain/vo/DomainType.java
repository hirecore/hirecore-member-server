package io.hirecore.hirecorememberserver.modules.file.domain.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.hirecore.hirecorememberserver.modules.file.domain.exception.ImageFileMetaDomainException;
import io.hirecore.hirecorememberserver.modules.file.domain.exception.ImageFileMetaDomainExceptionCodeCluster;

public enum DomainType {
    PORTFOLIO("portfolio"),
    RESUME("resume");

    private final String pathSegment;

    DomainType(String pathSegment) {
        this.pathSegment = pathSegment;
    }

    @JsonCreator
    public static DomainType from(String value) {
        for (DomainType domainType : values()) {
            if (domainType.pathSegment.equalsIgnoreCase(value)) {
                return domainType;
            }
        }

        throw new ImageFileMetaDomainException(
                ImageFileMetaDomainExceptionCodeCluster.HiddenDetailResponse.UNSUPPORTED_DOMAIN_TYPE
        );
    }

    @JsonValue
    public String getPathSegment() {
        return pathSegment;
    }
}

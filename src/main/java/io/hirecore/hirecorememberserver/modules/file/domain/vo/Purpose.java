package io.hirecore.hirecorememberserver.modules.file.domain.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.hirecore.hirecorememberserver.modules.file.domain.exception.ImageFileMetaDomainException;
import io.hirecore.hirecorememberserver.modules.file.domain.exception.ImageFileMetaDomainExceptionCodeCluster;

public enum Purpose {
    CONTENT_IMAGE("content-image"),
    THUMBNAIL_IMAGE("thumbnail-image");

    private final String pathSegment;

    Purpose(String pathSegment) {
        this.pathSegment = pathSegment;
    }

    @JsonCreator
    public static Purpose from(String value) {
        for (Purpose purpose : values()) {
            if (purpose.pathSegment.equalsIgnoreCase(value)) {
                return purpose;
            }
        }

        throw new ImageFileMetaDomainException(
                ImageFileMetaDomainExceptionCodeCluster.HiddenDetailResponse.UNSUPPORTED_PURPOSE
        );
    }

    @JsonValue
    public String getPathSegment() {
        return pathSegment;
    }
}

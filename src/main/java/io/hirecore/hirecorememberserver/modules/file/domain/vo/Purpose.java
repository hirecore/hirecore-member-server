package io.hirecore.hirecorememberserver.modules.file.domain.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.hirecore.hirecorememberserver.modules.file.domain.exception.ImageFileMetaDomainException;
import io.hirecore.hirecorememberserver.modules.file.domain.exception.ImageFileMetaDomainExceptionCodeCluster;

public enum Purpose {
    CONTENT_IMAGE("contentImage", "content-image"),
    THUMBNAIL_IMAGE("thumbnailImage", "thumbnail-image");

    private final String value;
    private final String pathSegment;

    Purpose(String value, String pathSegment) {
        this.value = value;
        this.pathSegment = pathSegment;
    }

    @JsonCreator
    public static Purpose from(String value) {
        for (Purpose purpose : values()) {
            if (purpose.value.equalsIgnoreCase(value)) {
                return purpose;
            }
        }

        throw new ImageFileMetaDomainException(
                ImageFileMetaDomainExceptionCodeCluster.HiddenDetailResponse.UNSUPPORTED_PURPOSE
        );
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public String getPathSegment() {
        return pathSegment;
    }
}

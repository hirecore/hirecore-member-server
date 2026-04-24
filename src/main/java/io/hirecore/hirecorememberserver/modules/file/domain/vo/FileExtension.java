package io.hirecore.hirecorememberserver.modules.file.domain.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.hirecore.hirecorememberserver.modules.file.domain.exception.ImageFileMetaDomainException;
import io.hirecore.hirecorememberserver.modules.file.domain.exception.ImageFileMetaDomainExceptionCodeCluster;

public enum FileExtension {
    WEBP("webp");

    private final String value;

    FileExtension(String value) {
        this.value = value;
    }

    @JsonCreator
    public static FileExtension from(String value) {
        for (FileExtension fileExtension : values()) {
            if (fileExtension.value.equalsIgnoreCase(value)) {
                return fileExtension;
            }
        }

        throw new ImageFileMetaDomainException(
                ImageFileMetaDomainExceptionCodeCluster.HiddenDetailResponse.UNSUPPORTED_FILE_EXTENSION
        );
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}

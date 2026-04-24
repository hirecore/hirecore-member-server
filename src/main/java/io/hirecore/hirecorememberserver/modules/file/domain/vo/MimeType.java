package io.hirecore.hirecorememberserver.modules.file.domain.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.hirecore.hirecorememberserver.modules.file.domain.exception.ImageFileMetaDomainException;
import io.hirecore.hirecorememberserver.modules.file.domain.exception.ImageFileMetaDomainExceptionCodeCluster;

public enum MimeType {
    IMAGE_WEBP("image/webp");

    private final String value;

    MimeType(String value) {
        this.value = value;
    }

    // 입력 -> 역직렬화
    @JsonCreator
    public static MimeType from(String value) {
        for (MimeType mimeType : values()) {
            if (mimeType.value.equals(value)) {
                return mimeType;
            }
        }

        throw new ImageFileMetaDomainException(
                ImageFileMetaDomainExceptionCodeCluster.HiddenDetailResponse.UNSUPPORTED_MIME_TYPE
        );
    }

    // 응답 -> 직렬화
    @JsonValue
    public String getValue() {
        return value;
    }
}

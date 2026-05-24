package io.hirecore.hirecorememberserver.sharedkernel.domain.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelExceptionCodeCluster.HiddenDetailResponse;

// todo: 클래스 명의 변경 고려 => ImageContentType
public enum Purpose {
    CONTENT_IMAGE("contentImage"),
    THUMBNAIL_IMAGE("thumbnailImage");

    private final String value;

    Purpose(String value) {
        this.value = value;
    }

    @JsonCreator
    public static Purpose from(String value) {
        for (Purpose purpose : values()) {
            if (purpose.value.equalsIgnoreCase(value)) {
                return purpose;
            }
        }
        throw new SharedKernelException(HiddenDetailResponse.PURPOSE_INVALID);
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}

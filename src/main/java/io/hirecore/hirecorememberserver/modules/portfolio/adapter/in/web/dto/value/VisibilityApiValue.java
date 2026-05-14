package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum VisibilityApiValue {
    PUBLIC("public"),
    PRIVATE("private");

    private final String value;

    VisibilityApiValue(String value) {
        this.value = value;
    }

    @JsonCreator
    public static VisibilityApiValue from(String value) {
        for (VisibilityApiValue v : values()) {
            if (v.value.equalsIgnoreCase(value)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Unknown Visibility: " + value);
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}

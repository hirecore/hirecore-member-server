package io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CollaborationTypeApiValue {
    TEAM("team"),
    PERSONAL("personal");

    private final String value;

    CollaborationTypeApiValue(String value) {
        this.value = value;
    }

    @JsonCreator
    public static CollaborationTypeApiValue from(String value) {
        for (CollaborationTypeApiValue v : values()) {
            if (v.value.equalsIgnoreCase(value)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Unknown CollaborationType: " + value);
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}

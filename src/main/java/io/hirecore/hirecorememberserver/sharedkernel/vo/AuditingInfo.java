package io.hirecore.hirecorememberserver.sharedkernel.vo;

import java.time.Instant;

public record AuditingInfo(
        Instant createdAt,
        Instant updatedAt
) {

    public static AuditingInfo create() {
        Instant now = Instant.now();
        return new AuditingInfo(now, now);
    }

    public AuditingInfo update() {
        return new AuditingInfo(this.createdAt, Instant.now());
    }
}

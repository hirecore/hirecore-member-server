package io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.annotations.Comment;

import java.time.Instant;

@Embeddable
public record AuditingJpaInfo(
        @Comment("생성 일시")
        @Column(updatable = false, nullable = false, columnDefinition = "DATETIME(6)")
        Instant createdAt,

        @Comment("수정 일시")
        @Column(nullable = false, columnDefinition = "DATETIME(6)")
        Instant updatedAt
){
}

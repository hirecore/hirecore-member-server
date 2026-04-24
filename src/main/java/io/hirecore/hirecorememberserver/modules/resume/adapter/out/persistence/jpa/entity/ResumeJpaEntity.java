package io.hirecore.hirecorememberserver.modules.resume.adapter.out.persistence.jpa.entity;

import io.hirecore.hirecorememberserver.sharedkernel.infrastructure.persistence.jpa.AbstractPersistableAggregateRoot;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "resumes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class ResumeJpaEntity extends AbstractPersistableAggregateRoot<Long> {
    @Id
    private Long id;
}

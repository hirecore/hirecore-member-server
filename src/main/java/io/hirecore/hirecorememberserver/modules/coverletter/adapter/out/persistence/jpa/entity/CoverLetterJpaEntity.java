package io.hirecore.hirecorememberserver.modules.coverletter.adapter.out.persistence.jpa.entity;

import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.AbstractPersistableAggregateRoot;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cover_letters")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class CoverLetterJpaEntity extends AbstractPersistableAggregateRoot<Long> {
    @Id
    private Long id;
}

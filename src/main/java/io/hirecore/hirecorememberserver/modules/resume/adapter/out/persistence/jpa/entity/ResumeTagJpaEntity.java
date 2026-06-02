package io.hirecore.hirecorememberserver.modules.resume.adapter.out.persistence.jpa.entity;

import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.AbstractPersistableEntity;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.AuditingJpaInfo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "resume_tags")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class ResumeTagJpaEntity extends AbstractPersistableEntity<Long> {
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private ResumeJpaEntity resume;

    @Comment("사용자 입력 태그")
    @Column(name = "user_input_tag", nullable = false, columnDefinition = "VARCHAR(50)")
    private String userInputTag;

    @Comment("정규화된 태그")
    @Column(name = "normalized_tag", nullable = false, columnDefinition = "VARCHAR(50)")
    private String normalizedTag;

    @Embedded
    private AuditingJpaInfo auditingInfo;
}

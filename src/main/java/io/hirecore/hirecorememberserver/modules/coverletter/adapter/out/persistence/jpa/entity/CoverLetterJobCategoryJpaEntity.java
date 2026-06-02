package io.hirecore.hirecorememberserver.modules.coverletter.adapter.out.persistence.jpa.entity;

import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.AbstractPersistableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.Instant;

@Entity
@Table(name = "cover_letter_job_categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class CoverLetterJobCategoryJpaEntity extends AbstractPersistableEntity<Long> {
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cover_letter_id", nullable = false)
    private CoverLetterJpaEntity coverLetter;

    @Comment("직무 카테고리 ID")
    @Column(name = "job_category_id", nullable = false)
    private Long jobCategoryId;

    @Comment("사용자가 입력한 자기소개서 카테고리 라벨")
    @Column(name = "user_input", columnDefinition = "VARCHAR(10)")
    private String userInput;

    @Comment("연결 시각")
    @Column(name = "connected_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant connectedAt;
}

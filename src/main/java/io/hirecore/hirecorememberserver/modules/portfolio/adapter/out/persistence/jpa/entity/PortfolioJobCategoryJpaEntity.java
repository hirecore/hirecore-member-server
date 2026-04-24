package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity;

import io.hirecore.hirecorememberserver.sharedkernel.infrastructure.persistence.jpa.AbstractPersistableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.Instant;

@Entity
@Table(name = "portfolio_job_categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class PortfolioJobCategoryJpaEntity extends AbstractPersistableEntity<Long> {
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private PortfolioJpaEntity portfolio;

    @Comment("직무 카테고리 ID")
    @Column(name = "job_category_id", nullable = false)
    private Long jobCategoryId;

    @Comment("사용자 정의 직무 카테고리명")
    @Column(name = "custom_job_category_name", columnDefinition = "VARCHAR(30)")
    private String customJobCategoryName;

    @Comment("삭제 여부")
    @Column(name = "is_deleted", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant isDeleted;

    @Comment("삭제 시각")
    @Column(name = "deleted_at", columnDefinition = "DATETIME(6)")
    private Instant deletedAt;

    @Comment("연결 시각")
    @Column(name = "connected_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant connectedAt;
}

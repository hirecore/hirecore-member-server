package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity;

import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.AbstractPersistableEntity;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private PortfolioJpaEntity portfolio;

    @Comment("직무 카테고리 ID")
    @Column(name = "job_category_id", nullable = false)
    private Long jobCategoryId;

    @Comment("사용자가 입력한 포트폴리오 카테고리 라벨")
    @Column(name = "user_input", columnDefinition = "VARCHAR(10)")
    private String userInput;

    @Comment("삭제 여부")
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Comment("삭제 시각")
    @Column(name = "deleted_at", columnDefinition = "DATETIME(6)")
    private Instant deletedAt;

    @Comment("연결 시각")
    @Column(name = "connected_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant connectedAt;

    void setPortfolio(PortfolioJpaEntity portfolio) {
        this.portfolio = portfolio;
    }
}

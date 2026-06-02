package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity;

import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.AbstractPersistableEntity;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.AuditingJpaInfo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "portfolio_tags")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class PortfolioTagJpaEntity extends AbstractPersistableEntity<Long> {
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private PortfolioJpaEntity portfolio;

    @Comment("사용자 입력 태그")
    @Column(name = "user_input_tag", nullable = false, columnDefinition = "VARCHAR(50)")
    private String userInputTag;

    @Comment("정규화된 태그")
    @Column(name = "normalized_tag", nullable = false, columnDefinition = "VARCHAR(50)")
    private String normalizedTag;

    @Comment("사용자 의도 정렬 순서 (0부터 시작)")
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Embedded
    private AuditingJpaInfo auditingInfo;

    public void attachPortfolio(PortfolioJpaEntity portfolio) {
        this.portfolio = portfolio;
    }
}

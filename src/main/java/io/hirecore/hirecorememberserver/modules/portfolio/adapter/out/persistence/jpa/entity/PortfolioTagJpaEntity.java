package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity;

import io.hirecore.hirecorememberserver.common.adapter.out.persistence.jpa.AbstractPersistableEntity;
import io.hirecore.hirecorememberserver.common.adapter.out.persistence.jpa.vo.AuditingJpaInfo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.Instant;

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

    @Comment("삭제 여부")
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Comment("삭제 시각")
    @Column(name = "deleted_at", columnDefinition = "DATETIME(6)")
    private Instant deletedAt;

    @Embedded
    private AuditingJpaInfo auditingInfo;
}

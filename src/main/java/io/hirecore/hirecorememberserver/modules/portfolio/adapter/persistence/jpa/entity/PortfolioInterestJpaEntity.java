package io.hirecore.hirecorememberserver.modules.portfolio.adapter.persistence.jpa.entity;

import io.hirecore.hirecorememberserver.common.adapter.out.persistence.jpa.AbstractPersistableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.Instant;

@Entity
@Table(
        name = "portfolio_interests",
        uniqueConstraints = @UniqueConstraint(columnNames = {"portfolio_id", "member_account_id"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class PortfolioInterestJpaEntity extends AbstractPersistableEntity<Long> {
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private PortfolioJpaEntity portfolio;

    @Comment("관심을 누른 회원 계정 ID")
    @Column(name = "member_account_id", nullable = false)
    private Long memberAccountId;

    @Comment("관심 등록 시각")
    @Column(name = "interest_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant interestAt;

    @Comment("삭제 여부")
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Comment("삭제 시각")
    @Column(name = "deleted_at", columnDefinition = "DATETIME(6)")
    private Instant deletedAt;
}

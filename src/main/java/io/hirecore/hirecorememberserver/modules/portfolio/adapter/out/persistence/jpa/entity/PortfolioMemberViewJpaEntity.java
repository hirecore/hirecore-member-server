package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity;

import io.hirecore.hirecorememberserver.sharedkernel.infrastructure.persistence.jpa.AbstractPersistableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.Instant;

@Entity
@Table(
        name = "portfolio_member_views",
        uniqueConstraints = @UniqueConstraint(columnNames = {"portfolio_id", "member_account_id"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class PortfolioMemberViewJpaEntity extends AbstractPersistableEntity<Long> {
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private PortfolioJpaEntity portfolio;

    @Comment("조회한 회원 계정 ID")
    @Column(name = "member_account_id", nullable = false)
    private Long memberAccountId;

    @Comment("조회 시각")
    @Column(name = "viewed_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant viewedAt;
}

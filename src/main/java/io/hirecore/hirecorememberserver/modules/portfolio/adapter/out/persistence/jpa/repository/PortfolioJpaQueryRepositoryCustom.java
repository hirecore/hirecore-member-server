package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioJpaEntity;

import java.time.Instant;
import java.util.List;

/**
 * QueryDSL 로 작성된 동적 조회 메서드 모음.
 * Spring Data JPA 관습에 따라 동일 패키지의 {@code *RepositoryImpl} 이 자동 결합된다.
 */
public interface PortfolioJpaQueryRepositoryCustom {

    /**
     * PUBLIC 포트폴리오를 (effective updatedAt desc, id desc) 순으로 가져온다.
     * effective updatedAt = GREATEST(Portfolio.updatedAt, PortfolioContent.updatedAt, PortfolioJobCategory.connectedAt, COALESCE(MAX(PortfolioTag.updatedAt), Portfolio.updatedAt)).
     *
     * <p>각 row 의 {@code effectiveUpdatedAt} 는 (effective, id) 복합 커서의 정렬 키로,
     * 응답 노출용으로도 그대로 사용된다.</p>
     */
    List<PortfolioWithEffectiveUpdatedAt> findPublicPortfoliosByCursor(
            Instant cursorEffectiveUpdatedAt,
            Long cursorPortfolioId,
            int limit
    );

    record PortfolioWithEffectiveUpdatedAt(
            PortfolioJpaEntity portfolio,
            Instant effectiveUpdatedAt
    ) {}
}

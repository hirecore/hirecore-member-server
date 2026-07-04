package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioJpaEntity;

import java.time.Instant;
import java.util.List;

// QueryDSL 동적 조회 (RepositoryImpl 자동 결합)
public interface PortfolioJpaQueryRepositoryCustom {

    // PUBLIC 포트폴리오를 (effective updatedAt desc, id desc) 커서 조회, effective는 GREATEST 집계값
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

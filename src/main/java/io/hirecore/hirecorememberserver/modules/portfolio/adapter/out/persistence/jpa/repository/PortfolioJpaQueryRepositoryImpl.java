package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.QPortfolioContentJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.QPortfolioJobCategoryJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.QPortfolioJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.QPortfolioTagJpaEntity;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class PortfolioJpaQueryRepositoryImpl implements PortfolioJpaQueryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 2-step: (A) GREATEST로 (id, effectiveUpdatedAt) 정렬·페이징 (커서는 집계라 HAVING), (B) id로 fetch-join 로드 후 A 순서로 재정렬
    @Override
    public List<PortfolioWithEffectiveUpdatedAt> findPublicPortfoliosByCursor(
            Instant cursorEffectiveUpdatedAt,
            Long cursorPortfolioId,
            int limit
    ) {
        QPortfolioJpaEntity p = QPortfolioJpaEntity.portfolioJpaEntity;
        QPortfolioContentJpaEntity pc = QPortfolioContentJpaEntity.portfolioContentJpaEntity;
        QPortfolioJobCategoryJpaEntity pjc = QPortfolioJobCategoryJpaEntity.portfolioJobCategoryJpaEntity;
        QPortfolioTagJpaEntity pt = QPortfolioTagJpaEntity.portfolioTagJpaEntity;

        DateTimeExpression<Instant> effective = Expressions.dateTimeTemplate(
                Instant.class,
                "GREATEST({0}, {1}, {2}, COALESCE({3}, {0}))",
                p.auditingInfo.updatedAt,
                pc.auditingInfo.updatedAt,
                pjc.connectedAt,
                pt.auditingInfo.updatedAt.max()
        );

        BooleanBuilder cursorPredicate = new BooleanBuilder();
        if (cursorEffectiveUpdatedAt != null && cursorPortfolioId != null) {
            cursorPredicate.and(
                    effective.lt(cursorEffectiveUpdatedAt)
                            .or(effective.eq(cursorEffectiveUpdatedAt).and(p.id.lt(cursorPortfolioId)))
            );
        }

        List<Tuple> tuples = queryFactory
                .select(p.id, effective)
                .from(p)
                .join(pc).on(pc.portfolio.eq(p))
                .join(pjc).on(pjc.portfolio.eq(p))
                .leftJoin(pt).on(pt.portfolio.eq(p))
                .where(p.visibility.eq(Visibility.PUBLIC))
                .groupBy(p.id, p.auditingInfo.updatedAt, pc.auditingInfo.updatedAt, pjc.connectedAt)
                .having(cursorPredicate)
                .orderBy(effective.desc(), p.id.desc())
                .limit(limit)
                .fetch();

        if (tuples.isEmpty()) {
            return List.of();
        }

        List<Long> orderedIds = tuples.stream().map(t -> t.get(p.id)).toList();
        Map<Long, Instant> effectiveById = new HashMap<>();
        for (Tuple t : tuples) {
            effectiveById.put(t.get(p.id), t.get(effective));
        }

        List<PortfolioJpaEntity> loaded = queryFactory
                .selectFrom(p)
                .leftJoin(p.portfolioContent).fetchJoin()
                .leftJoin(p.portfolioJobCategory).fetchJoin()
                .where(p.id.in(orderedIds))
                .fetch();

        Map<Long, PortfolioJpaEntity> entityById = new HashMap<>();
        for (PortfolioJpaEntity entity : loaded) {
            entityById.put(entity.getId(), entity);
        }

        return orderedIds.stream()
                .map(id -> new PortfolioWithEffectiveUpdatedAt(
                        entityById.get(id),
                        effectiveById.get(id)
                ))
                .toList();
    }
}

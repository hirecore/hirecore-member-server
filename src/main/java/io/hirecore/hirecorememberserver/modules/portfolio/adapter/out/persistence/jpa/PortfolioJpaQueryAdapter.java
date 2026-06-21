package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper.PortfolioJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository.PortfolioJpaQueryRepository;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository.PortfolioJpaQueryRepositoryCustom.PortfolioWithEffectiveUpdatedAt;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPublicPortfolioSummaryPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PortfolioJpaQueryAdapter implements
        LoadPortfolioPort,
        LoadPublicPortfolioSummaryPort
{

    private final PortfolioJpaQueryRepository portfolioJpaQueryRepository;
    private final PortfolioJpaEntityMapper portfolioJpaEntityMapper;

    @Override
    public Optional<Portfolio> findById(Long portfolioId) {
        return portfolioJpaQueryRepository.findById(portfolioId)
                .map(portfolioJpaEntityMapper::toDomain);
    }

    @Override
    public List<Portfolio> findAllByMemberAccountIdOrderByUpdatedAtDesc(Long memberAccountId) {
        return portfolioJpaQueryRepository.findAllByMemberAccountIdOrderByUpdatedAtDesc(memberAccountId).stream()
                .map(portfolioJpaEntityMapper::toDomain)
                .toList();
    }

    @Override
    public List<Portfolio> findAllPublicByMemberAccountIdExcludingOrderByUpdatedAtDesc(
            Long memberAccountId,
            Long excludedPortfolioId
    ) {
        return portfolioJpaQueryRepository
                .findAllPublicByMemberAccountIdExcludingOrderByUpdatedAtDesc(memberAccountId, excludedPortfolioId)
                .stream()
                .map(portfolioJpaEntityMapper::toDomain)
                .toList();
    }

    @Override
    public List<PublicPortfolioRow> findPublicPortfoliosOrderByEffectiveUpdatedAtDesc(
            Instant cursorEffectiveUpdatedAt,
            Long cursorPortfolioId,
            int limit
    ) {
        List<PortfolioWithEffectiveUpdatedAt> rows = portfolioJpaQueryRepository
                .findPublicPortfoliosByCursor(cursorEffectiveUpdatedAt, cursorPortfolioId, limit);
        return rows.stream()
                .map(row -> new PublicPortfolioRow(
                        portfolioJpaEntityMapper.toDomain(row.portfolio()),
                        row.effectiveUpdatedAt()
                ))
                .toList();
    }
}

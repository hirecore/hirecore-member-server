package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository.PortfolioMemberViewJpaQueryRepository;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.ExistsPortfolioMemberViewPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PortfolioMemberViewJpaQueryAdapter implements ExistsPortfolioMemberViewPort {

    private final PortfolioMemberViewJpaQueryRepository repository;

    @Override
    public boolean exists(Long portfolioId, Long memberAccountId) {
        return repository.existsByPortfolio_IdAndMemberAccountId(portfolioId, memberAccountId);
    }
}

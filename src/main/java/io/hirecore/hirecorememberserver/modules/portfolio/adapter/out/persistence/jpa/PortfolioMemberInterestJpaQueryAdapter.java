package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository.PortfolioMemberInterestJpaQueryRepository;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.ExistsPortfolioMemberInterestPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PortfolioMemberInterestJpaQueryAdapter implements ExistsPortfolioMemberInterestPort {

    private final PortfolioMemberInterestJpaQueryRepository repository;

    @Override
    public boolean exists(Long portfolioId, Long memberAccountId) {
        return repository.existsByPortfolio_IdAndMemberAccountId(portfolioId, memberAccountId);
    }
}

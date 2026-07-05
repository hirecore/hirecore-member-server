package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository.PortfolioMemberInterestJpaQueryRepository;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.ExistsPortfolioMemberInterestPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.FindInterestedPortfolioIdsPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class PortfolioMemberInterestJpaQueryAdapter implements
        ExistsPortfolioMemberInterestPort,
        FindInterestedPortfolioIdsPort
{

    private final PortfolioMemberInterestJpaQueryRepository repository;

    @Override
    public boolean exists(Long portfolioId, Long memberAccountId) {
        return repository.existsByPortfolio_IdAndMemberAccountId(portfolioId, memberAccountId);
    }

    @Override
    public Set<Long> findInterestedPortfolioIds(Collection<Long> portfolioIds, Long memberAccountId) {
        if (portfolioIds == null || portfolioIds.isEmpty()) {
            return Set.of();
        }
        return repository.findInterestedPortfolioIds(memberAccountId, portfolioIds);
    }
}

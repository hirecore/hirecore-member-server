package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper.PortfolioJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository.PortfolioJpaQueryRepository;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response.PortfolioTagResponse;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPortfolioTagPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PortfolioJpaQueryAdapter implements LoadPortfolioPort, LoadPortfolioTagPort {

    private final PortfolioJpaQueryRepository portfolioJpaQueryRepository;
    private final PortfolioJpaEntityMapper portfolioJpaEntityMapper;

    @Override
    public Optional<Portfolio> findPortfolio(Long portfolioId) {
        return portfolioJpaQueryRepository.findById(portfolioId)
                .map(portfolioJpaEntityMapper::toDomain);
    }

    @Override
    public List<PortfolioTagResponse> findTags(Long portfolioId) {
        return portfolioJpaQueryRepository.findTagsByPortfolioId(portfolioId).stream()
                .map(projection -> new PortfolioTagResponse(
                        projection.getUserInputTag(),
                        projection.getSortOrder()
                ))
                .toList();
    }
}

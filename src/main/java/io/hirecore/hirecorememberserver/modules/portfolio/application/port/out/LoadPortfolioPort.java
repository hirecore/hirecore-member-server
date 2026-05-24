package io.hirecore.hirecorememberserver.modules.portfolio.application.port.out;

import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;

import java.util.Optional;

public interface LoadPortfolioPort {
    Optional<Portfolio> findPortfolio(Long portfolioId);
}

package io.hirecore.hirecorememberserver.modules.portfolio.application.port.out;

import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;

public interface DeletePortfolioPort {
    void delete(Portfolio portfolio);
}

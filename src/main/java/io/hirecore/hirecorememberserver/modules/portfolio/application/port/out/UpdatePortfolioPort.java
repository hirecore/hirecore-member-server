package io.hirecore.hirecorememberserver.modules.portfolio.application.port.out;

import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;

public interface UpdatePortfolioPort {
    void update(Portfolio portfolio);
}

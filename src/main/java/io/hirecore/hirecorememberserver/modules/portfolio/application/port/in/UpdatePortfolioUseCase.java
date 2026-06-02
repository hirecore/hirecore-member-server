package io.hirecore.hirecorememberserver.modules.portfolio.application.port.in;

import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request.UpdatePortfolioCommand;

public interface UpdatePortfolioUseCase {
    Long execute(Long portfolioId, Long viewerId, UpdatePortfolioCommand command);
}

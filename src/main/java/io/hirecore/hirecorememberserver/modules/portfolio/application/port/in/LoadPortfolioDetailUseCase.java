package io.hirecore.hirecorememberserver.modules.portfolio.application.port.in;

import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response.PortfolioDetailResponse;

public interface LoadPortfolioDetailUseCase {
    PortfolioDetailResponse execute(Long portfolioId, Long viewerId);
}

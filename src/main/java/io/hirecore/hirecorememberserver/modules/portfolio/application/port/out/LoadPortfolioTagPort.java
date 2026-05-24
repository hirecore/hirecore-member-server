package io.hirecore.hirecorememberserver.modules.portfolio.application.port.out;

import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response.PortfolioTagResponse;

import java.util.List;

public interface LoadPortfolioTagPort {
    List<PortfolioTagResponse> findTags(Long portfolioId);
}

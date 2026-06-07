package io.hirecore.hirecorememberserver.modules.portfolio.application.port.in;

import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response.MyPortfolioSummariesResponse;

public interface LoadMyPortfolioSummariesUseCase {
    MyPortfolioSummariesResponse execute(Long viewerId);
}

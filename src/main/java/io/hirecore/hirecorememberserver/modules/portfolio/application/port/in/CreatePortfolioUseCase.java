package io.hirecore.hirecorememberserver.modules.portfolio.application.port.in;

import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request.CreatePortfolioCommand;

public interface CreatePortfolioUseCase {
    Long execute(Long memberId, CreatePortfolioCommand command);
}

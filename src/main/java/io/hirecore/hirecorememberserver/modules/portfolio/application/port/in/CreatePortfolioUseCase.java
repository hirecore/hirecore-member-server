package io.hirecore.hirecorememberserver.modules.portfolio.application.port.in;

import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request.CreatePortfolioCommand;

public interface CreatePortfolioUseCase {
    void execute(Long memberId, CreatePortfolioCommand command);
}

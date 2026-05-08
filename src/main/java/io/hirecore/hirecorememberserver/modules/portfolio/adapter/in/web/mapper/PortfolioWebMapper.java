package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.mapper;

import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.request.CreatePortfolioApiRequest;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.request.PortfolioContentApiRequest;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request.CreatePortfolioCommand;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request.PortfolioContentCommand;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapStructConfig.class)
public abstract class PortfolioWebMapper {
    public abstract CreatePortfolioCommand toCommand(CreatePortfolioApiRequest request);
    public abstract PortfolioContentCommand toContentCommand(PortfolioContentApiRequest content);
}

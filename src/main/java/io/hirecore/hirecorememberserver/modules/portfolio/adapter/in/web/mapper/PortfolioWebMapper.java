package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.mapper;

import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.request.CreatePortfolioApiRequest;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.request.PortfolioContentApiRequest;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.request.PortfolioTagApiRequest;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.value.CollaborationTypeApiValue;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.value.ExternalLinkApiValue;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.value.VisibilityApiValue;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request.CreatePortfolioCommand;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request.PortfolioContentCommand;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request.PortfolioTagCommand;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.CollaborationType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.ExternalLink;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapStructConfig.class)
public abstract class PortfolioWebMapper {
    public abstract CreatePortfolioCommand toCommand(CreatePortfolioApiRequest request);
    public abstract PortfolioContentCommand toContentCommand(PortfolioContentApiRequest content);
    public abstract PortfolioTagCommand toTagCommand(PortfolioTagApiRequest tag);

    public abstract CollaborationType toCollaborationType(CollaborationTypeApiValue source);
    public abstract Visibility toVisibility(VisibilityApiValue source);
    public abstract ExternalLink toExternalLink(ExternalLinkApiValue source);
}

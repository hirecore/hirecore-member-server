package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.mapper;

import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.request.CreatePortfolioApiRequest;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.request.JobCategoryApiRequest;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.request.PortfolioContentApiRequest;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.request.PortfolioExternalLinkApiRequest;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.request.PortfolioTagApiRequest;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.request.UpdatePortfolioApiRequest;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.response.*;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request.CreatePortfolioCommand;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request.JobCategoryCommand;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request.PortfolioContentCommand;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request.PortfolioExternalLinkCommand;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request.PortfolioTagCommand;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request.UpdatePortfolioCommand;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response.*;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.mapper.SharedDomainVoWebMapper;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(
        config = GlobalMapStructConfig.class,
        uses = {SharedDomainVoWebMapper.class}
)
public abstract class PortfolioWebMapper {
    public abstract CreatePortfolioCommand toCreatePortfolioCommand(CreatePortfolioApiRequest request);
    public abstract UpdatePortfolioCommand toUpdatePortfolioCommand(UpdatePortfolioApiRequest request);
    public abstract JobCategoryCommand toJobCategoryCommand(JobCategoryApiRequest jobCategory);
    public abstract PortfolioContentCommand toPortfolioContentCommand(PortfolioContentApiRequest content);
    public abstract PortfolioTagCommand toPortfolioTagCommand(PortfolioTagApiRequest tag);
    public abstract PortfolioExternalLinkCommand toPortfolioExternalLinkCommand(PortfolioExternalLinkApiRequest request);

    public abstract PortfolioDetailApiResponse toPortfolioDetailApiResponse(PortfolioDetailResponse response);
    public abstract PortfolioEditApiResponse toPortfolioEditApiResponse(PortfolioEditResponse response);
    public abstract PortfolioContentApiResponse toPortfolioContentApiResponse(PortfolioContentResponse response);
    public abstract PortfolioContentImageApiResponse toPortfolioContentImageApiResponse(PortfolioContentImageResponse response);
    public abstract PortfolioTagApiResponse toPortfolioTagApiResponse(PortfolioTagResponse response);
    public abstract PortfolioExternalLinkApiResponse toPortfolioExternalLinkApiResponse(PortfolioExternalLinkResponse response);
    public abstract PortfolioJobCategoryApiResponse toPortfolioJobCategoryApiResponse(PortfolioJobCategoryResponse response);
}

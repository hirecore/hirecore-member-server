package io.hirecore.hirecorememberserver.modules.category.adapter.in.web.mapper;

import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.category.adapter.in.web.dto.response.JobCategoryNodeApiResponse;
import io.hirecore.hirecorememberserver.modules.category.application.port.in.dto.response.JobCategoryNodeResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = GlobalMapStructConfig.class)
public abstract class JobCategoryWebMapper {
    public abstract List<JobCategoryNodeApiResponse> toApiResponses(List<JobCategoryNodeResponse> jobCategoryNodeResponses);
    public abstract JobCategoryNodeApiResponse toApiResponse(JobCategoryNodeResponse jobCategoryNodeResponse);
}

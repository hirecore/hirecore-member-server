package io.hirecore.hirecorememberserver.modules.category.adapter.in.web.mapper;

import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.category.adapter.in.web.dto.response.JobCategoryApiResponse;
import io.hirecore.hirecorememberserver.modules.category.application.port.in.dto.response.JobCategoryResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = GlobalMapStructConfig.class)
public abstract class JobCategoryWebMapper {
    public abstract List<JobCategoryApiResponse> toApiResponses(List<JobCategoryResponse> jobCategoryNodeResponses);
    public abstract JobCategoryApiResponse toApiResponse(JobCategoryResponse jobCategoryNodeResponse);
}

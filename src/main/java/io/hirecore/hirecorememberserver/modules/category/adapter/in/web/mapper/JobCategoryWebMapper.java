package io.hirecore.hirecorememberserver.modules.category.adapter.in.web.mapper;

import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.category.adapter.in.web.dto.JobCategoryApi;
import io.hirecore.hirecorememberserver.modules.category.application.port.in.LoadJobCategoryTreeUseCase;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = GlobalMapStructConfig.class)
public abstract class JobCategoryWebMapper {
    public abstract List<JobCategoryApi.Response> toApiResponses(List<LoadJobCategoryTreeUseCase.Response> jobCategoryNodeResponses);
    public abstract JobCategoryApi.Response toApiResponse(LoadJobCategoryTreeUseCase.Response jobCategoryNodeResponse);
}

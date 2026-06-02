package io.hirecore.hirecorememberserver.modules.category.application.mapper;

import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.category.application.port.in.dto.response.JobCategoryResponse;
import io.hirecore.hirecorememberserver.modules.category.domain.JobCategory;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapStructConfig.class)
public abstract class JobCategoryMapper {
    public abstract JobCategoryResponse toResponse(JobCategory jobCategory);
}

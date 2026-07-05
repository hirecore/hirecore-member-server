package io.hirecore.hirecorememberserver.modules.category.application.mapper;

import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.category.application.port.in.LoadJobCategoryTreeUseCase;
import io.hirecore.hirecorememberserver.modules.category.domain.JobCategory;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapStructConfig.class)
public abstract class JobCategoryMapper {
    public abstract LoadJobCategoryTreeUseCase.Response toResponse(JobCategory jobCategory);
}

package io.hirecore.hirecorememberserver.modules.category.application.port.in;

import io.hirecore.hirecorememberserver.modules.category.application.port.in.dto.response.JobCategoryResponse;

import java.util.List;

public interface LoadJobCategoryTreeUseCase {
    List<JobCategoryResponse> execute(Integer maxDepth);
}

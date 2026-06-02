package io.hirecore.hirecorememberserver.modules.category.application.port.in;

import io.hirecore.hirecorememberserver.modules.category.application.port.in.dto.response.JobCategoryNodeResponse;

import java.util.List;

public interface LoadJobCategoriesUseCase {
    List<JobCategoryNodeResponse> loadAllWithinDepth(Integer maxDepth);
}

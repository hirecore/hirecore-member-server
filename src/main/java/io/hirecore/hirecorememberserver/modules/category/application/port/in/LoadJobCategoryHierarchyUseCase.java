package io.hirecore.hirecorememberserver.modules.category.application.port.in;

import io.hirecore.hirecorememberserver.modules.category.domain.JobCategory;

import java.util.List;

public interface LoadJobCategoryHierarchyUseCase {
    List<JobCategory> loadHierarchyFromLeaf(Long leafJobCategoryId);
}

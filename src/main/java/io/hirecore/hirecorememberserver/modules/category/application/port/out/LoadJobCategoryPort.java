package io.hirecore.hirecorememberserver.modules.category.application.port.out;

import io.hirecore.hirecorememberserver.modules.category.domain.JobCategory;

import java.util.List;

public interface LoadJobCategoryPort {
    List<JobCategory> loadAllWithinDepth(Integer depth);
}

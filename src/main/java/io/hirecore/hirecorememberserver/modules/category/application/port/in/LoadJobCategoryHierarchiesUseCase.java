package io.hirecore.hirecorememberserver.modules.category.application.port.in;

import io.hirecore.hirecorememberserver.modules.category.domain.JobCategory;

import java.util.Collection;
import java.util.List;
import java.util.Map;

// 여러 leaf 직무 카테고리의 계층 경로 일괄 조회
public interface LoadJobCategoryHierarchiesUseCase {
    Map<Long, List<JobCategory>> execute(Collection<Long> leafJobCategoryIds);
}

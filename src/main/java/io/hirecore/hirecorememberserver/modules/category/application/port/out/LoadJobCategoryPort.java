package io.hirecore.hirecorememberserver.modules.category.application.port.out;

import io.hirecore.hirecorememberserver.modules.category.domain.JobCategory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LoadJobCategoryPort {
    List<JobCategory> findAllWithinDepth(Integer depth);
    Optional<JobCategory> findByCategoryCode(String categoryCode);
    Optional<JobCategory> findById(Long id);

    // leaf → root 계층 경로 (순서 root → leaf, 없으면 빈 리스트)
    List<JobCategory> findHierarchyByLeafId(Long leafJobCategoryId);

    // 여러 leaf 의 계층 경로 일괄 조회 (입력 비면 빈 Map)
    Map<Long, List<JobCategory>> findHierarchiesByLeafIds(Collection<Long> leafJobCategoryIds);
}

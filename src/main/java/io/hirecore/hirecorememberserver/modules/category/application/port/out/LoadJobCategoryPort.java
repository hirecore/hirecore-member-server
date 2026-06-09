package io.hirecore.hirecorememberserver.modules.category.application.port.out;

import io.hirecore.hirecorememberserver.modules.category.domain.JobCategory;

import java.util.List;
import java.util.Optional;

public interface LoadJobCategoryPort {
    List<JobCategory> loadAllWithinDepth(Integer depth);
    Optional<JobCategory> loadByCategoryCode(String categoryCode);
    Optional<JobCategory> loadById(Long id);

    /**
     * leaf id 의 카테고리부터 root 까지의 계층 경로를 단일 쿼리로 반환한다.
     *
     * <p>반환 리스트의 순서는 root → leaf 이며, leaf 가 존재하지 않으면 빈 리스트.</p>
     */
    List<JobCategory> loadHierarchyByLeafId(Long leafJobCategoryId);
}

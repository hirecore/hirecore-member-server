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

    /**
     * leaf id 의 카테고리부터 root 까지의 계층 경로를 단일 쿼리로 반환한다.
     *
     * <p>반환 리스트의 순서는 root → leaf 이며, leaf 가 존재하지 않으면 빈 리스트.</p>
     */
    List<JobCategory> findHierarchyByLeafId(Long leafJobCategoryId);

    /**
     * 여러 leaf id 각각의 계층 경로를 단일 쿼리로 일괄 반환한다.
     *
     * <p>반환 Map 의 키는 입력된 leaf id, 값은 해당 leaf 의 root → leaf 순 경로다.
     * 존재하지 않는 leaf 는 결과에 포함되지 않는다. 입력이 비어 있으면 SQL 을 발행하지 않고 빈 Map.</p>
     */
    Map<Long, List<JobCategory>> findHierarchiesByLeafIds(Collection<Long> leafJobCategoryIds);
}

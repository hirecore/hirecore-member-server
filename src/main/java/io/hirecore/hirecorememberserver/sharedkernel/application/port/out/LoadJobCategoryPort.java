package io.hirecore.hirecorememberserver.sharedkernel.application.port.out;

import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.dto.response.PortfolioJobCategoryHierarchyResult;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface LoadJobCategoryPort {
    Long findIdByCode(String categoryCode);
    List<PortfolioJobCategoryHierarchyResult> findJobCategoryHierarchy(Long leafJobCategoryId);

    /**
     * 여러 leaf 직무 카테고리의 계층 경로를 단일 쿼리로 일괄 조회한다.
     *
     * <p>반환 Map 의 키는 입력 leaf id, 값은 root → leaf 순서의 경로 (depth 오름차순) 다.
     * 존재하지 않는 leaf 는 결과에 포함되지 않는다.</p>
     */
    Map<Long, List<PortfolioJobCategoryHierarchyResult>> findJobCategoryHierarchies(Collection<Long> leafJobCategoryIds);
}

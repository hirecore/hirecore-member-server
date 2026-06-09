package io.hirecore.hirecorememberserver.modules.category.application.port.in;

import io.hirecore.hirecorememberserver.modules.category.domain.JobCategory;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 여러 leaf 직무 카테고리의 계층 경로를 일괄 조회하는 UseCase.
 *
 * <p>반환 Map 의 키는 입력 leaf id, 값은 root → leaf 순서의 경로다.
 * 존재하지 않는 leaf 는 결과에 포함되지 않는다.</p>
 */
public interface LoadJobCategoryHierarchiesUseCase {
    Map<Long, List<JobCategory>> execute(Collection<Long> leafJobCategoryIds);
}

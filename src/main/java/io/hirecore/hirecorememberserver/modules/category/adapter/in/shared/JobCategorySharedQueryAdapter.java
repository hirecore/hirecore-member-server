package io.hirecore.hirecorememberserver.modules.category.adapter.in.shared;

import io.hirecore.hirecorememberserver.modules.category.application.port.in.LoadJobCategoryHierarchiesUseCase;
import io.hirecore.hirecorememberserver.modules.category.application.port.in.LoadJobCategoryHierarchyUseCase;
import io.hirecore.hirecorememberserver.modules.category.application.port.in.LoadJobCategoryIdByCodeUseCase;
import io.hirecore.hirecorememberserver.modules.category.domain.JobCategory;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadJobCategorySharedPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JobCategorySharedQueryAdapter implements LoadJobCategorySharedPort {

    private final LoadJobCategoryIdByCodeUseCase loadJobCategoryIdByCodeUseCase;
    private final LoadJobCategoryHierarchyUseCase findJobCategoryHierarchyUseCase;
    private final LoadJobCategoryHierarchiesUseCase findJobCategoryHierarchiesUseCase;

    @Override
    public Long findIdByCode(String categoryCode) {
        return loadJobCategoryIdByCodeUseCase.execute(categoryCode);
    }

    @Override
    public List<LoadJobCategorySharedPort.Result> findJobCategoryHierarchy(Long leafJobCategoryId) {
        return findJobCategoryHierarchyUseCase.execute(leafJobCategoryId).stream()
                .map(JobCategorySharedQueryAdapter::toResult)
                .toList();
    }

    @Override
    public Map<Long, List<LoadJobCategorySharedPort.Result>> findJobCategoryHierarchies(Collection<Long> leafJobCategoryIds) {
        return findJobCategoryHierarchiesUseCase.execute(leafJobCategoryIds).entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(JobCategorySharedQueryAdapter::toResult)
                                .toList(),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    private static LoadJobCategorySharedPort.Result toResult(JobCategory category) {
        return new LoadJobCategorySharedPort.Result(
                category.getId(),
                category.getDepth().longValue(),
                category.getCategoryCode(),
                category.getCategoryName()
        );
    }
}

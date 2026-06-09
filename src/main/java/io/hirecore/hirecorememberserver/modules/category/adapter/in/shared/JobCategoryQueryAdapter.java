package io.hirecore.hirecorememberserver.modules.category.adapter.in.shared;

import io.hirecore.hirecorememberserver.modules.category.application.port.in.LoadJobCategoryHierarchiesUseCase;
import io.hirecore.hirecorememberserver.modules.category.application.port.in.LoadJobCategoryHierarchyUseCase;
import io.hirecore.hirecorememberserver.modules.category.application.port.in.LoadJobCategoryIdByCodeUseCase;
import io.hirecore.hirecorememberserver.modules.category.domain.JobCategory;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadJobCategoryPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.dto.response.PortfolioJobCategoryHierarchyResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JobCategoryQueryAdapter implements LoadJobCategoryPort {

    private final LoadJobCategoryIdByCodeUseCase loadJobCategoryIdByCodeUseCase;
    private final LoadJobCategoryHierarchyUseCase loadJobCategoryHierarchyUseCase;
    private final LoadJobCategoryHierarchiesUseCase loadJobCategoryHierarchiesUseCase;

    @Override
    public Long findIdByCode(String categoryCode) {
        return loadJobCategoryIdByCodeUseCase.execute(categoryCode);
    }

    @Override
    public List<PortfolioJobCategoryHierarchyResult> loadJobCategoryHierarchy(Long leafJobCategoryId) {
        return loadJobCategoryHierarchyUseCase.execute(leafJobCategoryId).stream()
                .map(JobCategoryQueryAdapter::toResult)
                .toList();
    }

    @Override
    public Map<Long, List<PortfolioJobCategoryHierarchyResult>> loadJobCategoryHierarchies(Collection<Long> leafJobCategoryIds) {
        return loadJobCategoryHierarchiesUseCase.execute(leafJobCategoryIds).entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(JobCategoryQueryAdapter::toResult)
                                .toList(),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    private static PortfolioJobCategoryHierarchyResult toResult(JobCategory category) {
        return new PortfolioJobCategoryHierarchyResult(
                category.getId(),
                category.getDepth().longValue(),
                category.getCategoryCode(),
                category.getCategoryName()
        );
    }
}

package io.hirecore.hirecorememberserver.modules.category.adapter.in.shared;

import io.hirecore.hirecorememberserver.modules.category.application.port.in.LoadJobCategoryHierarchyUseCase;
import io.hirecore.hirecorememberserver.modules.category.application.port.in.LoadJobCategoryIdByCodeUseCase;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadJobCategoryPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.dto.response.PortfolioJobCategoryHierarchyResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JobCategoryQueryAdapter implements LoadJobCategoryPort {

    private final LoadJobCategoryIdByCodeUseCase loadJobCategoryIdByCodeUseCase;
    private final LoadJobCategoryHierarchyUseCase loadJobCategoryHierarchyUseCase;

    @Override
    public Long findIdByCode(String categoryCode) {
        return loadJobCategoryIdByCodeUseCase.execute(categoryCode);
    }

    @Override
    public List<PortfolioJobCategoryHierarchyResult> loadJobCategoryHierarchy(Long leafJobCategoryId) {
        return loadJobCategoryHierarchyUseCase.execute(leafJobCategoryId).stream()
                .map(category -> new PortfolioJobCategoryHierarchyResult(
                        category.getId(),
                        category.getDepth().longValue(),
                        category.getCategoryCode(),
                        category.getCategoryName()
                ))
                .toList();
    }
}

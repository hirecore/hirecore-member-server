package io.hirecore.hirecorememberserver.modules.category.application.usecase;

import io.hirecore.hirecorememberserver.modules.category.application.port.in.LoadJobCategoryHierarchiesUseCase;
import io.hirecore.hirecorememberserver.modules.category.application.port.out.LoadJobCategoryPort;
import io.hirecore.hirecorememberserver.modules.category.domain.JobCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LoadJobCategoryHierarchiesUseCaseImpl implements LoadJobCategoryHierarchiesUseCase {

    private final LoadJobCategoryPort loadJobCategoryPort;

    @Override
    @Transactional(readOnly = true)
    public Map<Long, List<JobCategory>> execute(Collection<Long> leafJobCategoryIds) {
        if (leafJobCategoryIds == null || leafJobCategoryIds.isEmpty()) {
            return Map.of();
        }
        return loadJobCategoryPort.findHierarchiesByLeafIds(leafJobCategoryIds);
    }
}

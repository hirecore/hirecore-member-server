package io.hirecore.hirecorememberserver.modules.category.application.usecase;

import io.hirecore.hirecorememberserver.modules.category.application.JobCategoryQueryService;
import io.hirecore.hirecorememberserver.modules.category.application.mapper.JobCategoryNodeMapper;
import io.hirecore.hirecorememberserver.modules.category.application.port.in.LoadJobCategoriesUseCase;
import io.hirecore.hirecorememberserver.modules.category.application.port.in.dto.response.JobCategoryNodeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class LoadJobCategoriesUseCaseImpl implements LoadJobCategoriesUseCase {

    private final JobCategoryQueryService jobCategoryQueryService;
    private final JobCategoryNodeMapper jobCategoryNodeMapper;

    @Override
    @Transactional(readOnly = true)
    public List<JobCategoryNodeResponse> execute(Integer maxDepth) {
        return jobCategoryQueryService.loadByDynamicDepth(maxDepth).stream()
                .map(jobCategoryNodeMapper::toResponse)
                .toList();
    }
}

package io.hirecore.hirecorememberserver.modules.category.application.usecase;

import io.hirecore.hirecorememberserver.modules.category.application.exception.CategoryApplicationException;
import io.hirecore.hirecorememberserver.modules.category.application.exception.CategoryApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.category.application.port.in.LoadJobCategoryIdByCodeUseCase;
import io.hirecore.hirecorememberserver.modules.category.application.port.out.LoadJobCategoryPort;
import io.hirecore.hirecorememberserver.modules.category.domain.JobCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoadJobCategoryIdByCodeUseCaseImpl implements LoadJobCategoryIdByCodeUseCase {

    private final LoadJobCategoryPort loadJobCategoryPort;

    @Override
    @Transactional(readOnly = true)
    public Long execute(String categoryCode) {
        return loadJobCategoryPort.findByCategoryCode(categoryCode)
                .map(JobCategory::getId)
                .orElseThrow(() -> new CategoryApplicationException(
                        CategoryApplicationExceptionCodeCluster.DetailResponse.JOB_CATEGORY_CODE_NOT_FOUND
                ));
    }
}

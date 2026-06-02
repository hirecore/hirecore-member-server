package io.hirecore.hirecorememberserver.modules.category.application.usecase;

import io.hirecore.hirecorememberserver.modules.category.application.exception.CategoryApplicationException;
import io.hirecore.hirecorememberserver.modules.category.application.exception.CategoryApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.category.application.port.in.LoadJobCategoryHierarchyUseCase;
import io.hirecore.hirecorememberserver.modules.category.application.port.out.LoadJobCategoryPort;
import io.hirecore.hirecorememberserver.modules.category.domain.JobCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoadJobCategoryHierarchyUseCaseImpl implements LoadJobCategoryHierarchyUseCase {

    private final LoadJobCategoryPort loadJobCategoryPort;

    @Override
    @Transactional(readOnly = true)
    public List<JobCategory> execute(Long leafJobCategoryId) {
        LinkedList<JobCategory> hierarchy = new LinkedList<>();
        Long currentId = leafJobCategoryId;
        while (currentId != null) {
            JobCategory current = loadJobCategoryPort.loadById(currentId)
                    .orElseThrow(() -> new CategoryApplicationException(
                            CategoryApplicationExceptionCodeCluster.DetailResponse.JOB_CATEGORY_NOT_FOUND
                    ));
            hierarchy.addFirst(current);
            currentId = current.getParentId();
        }
        return hierarchy;
    }
}

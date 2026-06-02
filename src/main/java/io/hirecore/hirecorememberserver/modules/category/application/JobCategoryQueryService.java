package io.hirecore.hirecorememberserver.modules.category.application;

import io.hirecore.hirecorememberserver.modules.category.application.exception.CategoryApplicationException;
import io.hirecore.hirecorememberserver.modules.category.application.exception.CategoryApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.category.application.mapper.JobCategoryNodeMapper;
import io.hirecore.hirecorememberserver.modules.category.application.port.in.LoadJobCategoriesUseCase;
import io.hirecore.hirecorememberserver.modules.category.application.port.in.LoadJobCategoryHierarchyUseCase;
import io.hirecore.hirecorememberserver.modules.category.application.port.in.LoadJobCategoryIdByCodeUseCase;
import io.hirecore.hirecorememberserver.modules.category.application.port.in.dto.response.JobCategoryNodeResponse;
import io.hirecore.hirecorememberserver.modules.category.application.port.out.LoadJobCategoryPort;
import io.hirecore.hirecorememberserver.modules.category.domain.JobCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

/**
 * category BC 의 모든 Query 책임을 응집한 서비스.
 *
 * <p>BC 단위 *QueryService 컨벤션에 따라 세 in-port({@link LoadJobCategoriesUseCase},
 * {@link LoadJobCategoryHierarchyUseCase}, {@link LoadJobCategoryIdByCodeUseCase}) 를 한 클래스에서 구현합니다.
 * 각 메서드는 {@code execute(...)} 시그니처가 인자 타입으로 구분되어 오버로드됩니다.</p>
 */
@Service
@RequiredArgsConstructor
public class JobCategoryQueryService implements
        LoadJobCategoriesUseCase,
        LoadJobCategoryHierarchyUseCase,
        LoadJobCategoryIdByCodeUseCase {

    private final LoadJobCategoryPort loadJobCategoryPort;
    private final JobCategoryNodeMapper jobCategoryNodeMapper;

    @Override
    @Transactional(readOnly = true)
    public List<JobCategoryNodeResponse> execute(Integer maxDepth) {
        return loadJobCategoryPort.loadAllWithinDepth(maxDepth).stream()
                .map(jobCategoryNodeMapper::toResponse)
                .toList();
    }

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

    @Override
    @Transactional(readOnly = true)
    public Long execute(String categoryCode) {
        return loadJobCategoryPort.loadByCategoryCode(categoryCode)
                .map(JobCategory::getId)
                .orElseThrow(() -> new CategoryApplicationException(
                        CategoryApplicationExceptionCodeCluster.DetailResponse.JOB_CATEGORY_CODE_NOT_FOUND
                ));
    }
}

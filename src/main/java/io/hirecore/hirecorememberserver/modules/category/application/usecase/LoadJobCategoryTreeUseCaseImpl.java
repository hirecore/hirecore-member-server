package io.hirecore.hirecorememberserver.modules.category.application.usecase;

import io.hirecore.hirecorememberserver.modules.category.application.mapper.JobCategoryMapper;
import io.hirecore.hirecorememberserver.modules.category.application.port.in.LoadJobCategoryTreeUseCase;
import io.hirecore.hirecorememberserver.modules.category.application.port.out.LoadJobCategoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class LoadJobCategoryTreeUseCaseImpl implements LoadJobCategoryTreeUseCase {

    private final LoadJobCategoryPort loadJobCategoryPort;
    private final JobCategoryMapper jobCategoryMapper;

    @Override
    @Transactional(readOnly = true)
    public List<LoadJobCategoryTreeUseCase.Response> execute(Integer maxDepth) {
        return loadJobCategoryPort.findAllWithinDepth(maxDepth).stream()
                .map(jobCategoryMapper::toResponse)
                .toList();
    }
}

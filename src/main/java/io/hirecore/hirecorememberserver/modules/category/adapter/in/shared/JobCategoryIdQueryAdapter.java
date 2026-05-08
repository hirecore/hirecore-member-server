package io.hirecore.hirecorememberserver.modules.category.adapter.in.shared;

import io.hirecore.hirecorememberserver.modules.category.application.port.in.LoadJobCategoryIdByCodeUseCase;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadJobCategoryIdByCodePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobCategoryIdQueryAdapter implements LoadJobCategoryIdByCodePort {

    private final LoadJobCategoryIdByCodeUseCase loadJobCategoryIdByCodeUseCase;

    @Override
    public Long findIdByCode(String categoryCode) {
        return loadJobCategoryIdByCodeUseCase.execute(categoryCode);
    }
}

package io.hirecore.hirecorememberserver.modules.category.application;

import io.hirecore.hirecorememberserver.modules.category.application.port.out.LoadJobCategoryPort;
import io.hirecore.hirecorememberserver.modules.category.domain.JobCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobCategoryQueryService {

    private final LoadJobCategoryPort loadJobCategoryPort;

    public List<JobCategory> loadByDynamicDepth(Integer depth) {
        return loadJobCategoryPort.loadByDynamicDepth(depth);
    }
}

package io.hirecore.hirecorememberserver.modules.category.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.category.adapter.out.persistence.jpa.mapper.JobCategoryJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.category.adapter.out.persistence.jpa.repository.JobCategoryJpaQueryRepository;
import io.hirecore.hirecorememberserver.modules.category.application.port.out.LoadJobCategoryPort;
import io.hirecore.hirecorememberserver.modules.category.domain.JobCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JobCategoryJpaQueryAdapter implements LoadJobCategoryPort {

    private final JobCategoryJpaQueryRepository jobCategoryJpaQueryRepository;
    private final JobCategoryJpaEntityMapper jobCategoryJpaEntityMapper;


    @Override
    public List<JobCategory> loadAllWithinDepth(Integer depth) {
        return jobCategoryJpaQueryRepository.loadAllWithinDepth(depth).stream()
                .map(jobCategoryJpaEntityMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<JobCategory> loadByCategoryCode(String categoryCode) {
        return jobCategoryJpaQueryRepository.findActiveByCategoryCode(categoryCode)
                .map(jobCategoryJpaEntityMapper::toDomain);
    }

    @Override
    public Optional<JobCategory> loadById(Long id) {
        return jobCategoryJpaQueryRepository.findById(id)
                .map(jobCategoryJpaEntityMapper::toDomain);
    }
}

package io.hirecore.hirecorememberserver.modules.resume.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.resume.adapter.out.persistence.jpa.entity.ResumeJpaEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ResumeJpaQueryRepository extends Repository<ResumeJpaEntity, Long> {

    @Query("SELECT r.id AS id, r.title AS title FROM ResumeJpaEntity r WHERE r.id IN :ids")
    List<ResumeTitleProjection> findTitleProjectionsByIds(@Param("ids") Collection<Long> ids);

    interface ResumeTitleProjection {
        Long getId();
        String getTitle();
    }
}

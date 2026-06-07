package io.hirecore.hirecorememberserver.modules.coverletter.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.coverletter.adapter.out.persistence.jpa.entity.CoverLetterJpaEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface CoverLetterJpaQueryRepository extends Repository<CoverLetterJpaEntity, Long> {

    @Query("SELECT c.id AS id, c.title AS title FROM CoverLetterJpaEntity c WHERE c.id IN :ids")
    List<CoverLetterTitleProjection> findTitleProjectionsByIds(@Param("ids") Collection<Long> ids);

    interface CoverLetterTitleProjection {
        Long getId();
        String getTitle();
    }
}

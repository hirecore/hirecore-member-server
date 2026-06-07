package io.hirecore.hirecorememberserver.modules.coverletter.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.coverletter.adapter.out.persistence.jpa.entity.CoverLetterJpaEntity;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CoverLetterJpaQueryRepository extends Repository<CoverLetterJpaEntity, Long> {

    @Query("SELECT c.id AS id, c.title AS title FROM CoverLetterJpaEntity c WHERE c.id IN :ids")
    List<CoverLetterTitleProjection> findTitleProjectionsByIds(@Param("ids") Collection<Long> ids);

    @Query("""
            SELECT c.id              AS id,
                   c.title           AS title,
                   c.memberAccountId AS memberAccountId,
                   c.visibility      AS visibility,
                   cc.contentJson    AS contentJson,
                   cc.contentHtml    AS contentHtml
              FROM CoverLetterJpaEntity c
              JOIN c.coverLetterContent cc
             WHERE c.id = :id
            """)
    Optional<CoverLetterContentProjection> findContentProjectionById(@Param("id") Long id);

    interface CoverLetterTitleProjection {
        Long getId();
        String getTitle();
    }

    interface CoverLetterContentProjection {
        Long getId();
        String getTitle();
        Long getMemberAccountId();
        Visibility getVisibility();
        String getContentJson();
        String getContentHtml();
    }
}

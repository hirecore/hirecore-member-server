package io.hirecore.hirecorememberserver.modules.resume.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.resume.adapter.out.persistence.jpa.entity.ResumeJpaEntity;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ResumeJpaQueryRepository extends Repository<ResumeJpaEntity, Long> {

    @Query("SELECT r.id AS id, r.title AS title FROM ResumeJpaEntity r WHERE r.id IN :ids")
    List<ResumeTitleProjection> findTitleProjectionsByIds(@Param("ids") Collection<Long> ids);

    @Query("""
            SELECT r.id              AS id,
                   r.title           AS title,
                   r.memberAccountId AS memberAccountId,
                   r.visibility      AS visibility,
                   rc.contentJson    AS contentJson,
                   rc.contentHtml    AS contentHtml
              FROM ResumeJpaEntity r
              JOIN r.resumeContent rc
             WHERE r.id = :id
            """)
    Optional<ResumeContentProjection> findContentProjectionById(@Param("id") Long id);

    interface ResumeTitleProjection {
        Long getId();
        String getTitle();
    }

    interface ResumeContentProjection {
        Long getId();
        String getTitle();
        Long getMemberAccountId();
        Visibility getVisibility();
        String getContentJson();
        String getContentHtml();
    }
}

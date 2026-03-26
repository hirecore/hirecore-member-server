package io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.entity.MemberAccountJpaEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface MemberAccountJpaCommandRepository extends Repository<MemberAccountJpaEntity, Long> {
    MemberAccountJpaEntity save(MemberAccountJpaEntity memberAccountJpaEntity);

    @Modifying
    @Query("UPDATE MemberAccountJpaEntity m SET m.tokenVersion = m.tokenVersion + 1 WHERE m.id = :id")
    int incrementTokenVersion(@Param("id") Long id);
}

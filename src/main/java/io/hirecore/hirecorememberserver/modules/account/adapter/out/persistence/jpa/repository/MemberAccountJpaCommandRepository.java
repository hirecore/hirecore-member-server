package io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.entity.MemberAccountJpaEntity;
import org.springframework.data.repository.Repository;

public interface MemberAccountJpaCommandRepository extends Repository<MemberAccountJpaEntity, Long> {
    MemberAccountJpaEntity save(MemberAccountJpaEntity memberAccountJpaEntity);
}

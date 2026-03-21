package io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.entity.MemberAccountJpaEntity;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface MemberAccountJpaQueryRepository extends Repository<MemberAccountJpaEntity, Long> {
    Optional<MemberAccountJpaEntity> findById(Long id);
    Optional<MemberAccountJpaEntity> findByEmail(String email);
}

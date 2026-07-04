package io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.entity.MemberAccountJpaEntity;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.mapper.MemberAccountJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.repository.MemberAccountJpaCommandRepository;
import io.hirecore.hirecorememberserver.modules.account.domain.MemberAccount;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.SaveMemberAccountPort;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.IncrementTokenVersionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;


// SaveMemberAccountPort, IncrementTokenVersionPort JPA 구현 (POJO 이벤트 브리지)
@Component
@RequiredArgsConstructor
public class MemberAccountJpaCommandAdapter implements
        SaveMemberAccountPort,
        IncrementTokenVersionPort
{

    private final MemberAccountJpaEntityMapper memberAccountJpaEntityMapper;
    private final MemberAccountJpaCommandRepository memberAccountJpaCommandRepository;

    // POJO 이벤트를 엔티티로 이전 후 저장 (save 시 @DomainEvents 자동 발행)
    @Override
    public MemberAccount save(MemberAccount memberAccount) {
        MemberAccountJpaEntity entity = memberAccountJpaEntityMapper.toJpaEntity(memberAccount);

        Collection<Object> domainEvents = memberAccount.pollAllEvents();
        if (domainEvents != null && !domainEvents.isEmpty()) {
            domainEvents.forEach(entity::recordPersistenceEvent);
        }

        MemberAccountJpaEntity savedEntity = memberAccountJpaCommandRepository.save(entity);
        return memberAccountJpaEntityMapper.toDomain(savedEntity);
    }

    @Override
    public void incrementTokenVersion(Long memberId) {
        memberAccountJpaCommandRepository.incrementTokenVersion(memberId);
    }
}

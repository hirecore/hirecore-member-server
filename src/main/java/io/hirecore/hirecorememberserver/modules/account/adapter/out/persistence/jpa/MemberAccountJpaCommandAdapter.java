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


/**
 * {@link SaveMemberAccountPort}, {@link IncrementTokenVersionPort}의 JPA 구현체.
 *
 * <p>도메인 POJO를 JPA 엔티티로 변환하여 저장하며,
 * POJO에 누적된 도메인 이벤트를 JPA 엔티티로 이전(Event Bridge)하는 책임을 담당합니다.</p>
 */
@Component
@RequiredArgsConstructor
public class MemberAccountJpaCommandAdapter implements SaveMemberAccountPort, IncrementTokenVersionPort {

    private final MemberAccountJpaEntityMapper memberAccountJpaEntityMapper;
    private final MemberAccountJpaCommandRepository memberAccountJpaCommandRepository;

    /**
     * 도메인 POJO를 JPA 엔티티로 변환하여 저장합니다.
     *
     * <p>POJO에 누적된 도메인 이벤트를 JPA 엔티티로 이전(Event Bridge)한 뒤 저장하며,
     * {@code repository.save()} 호출 시 Spring Data가
     * {@link org.springframework.data.domain.DomainEvents @DomainEvents}를 통해 이벤트를 자동 발행합니다.</p>
     */
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

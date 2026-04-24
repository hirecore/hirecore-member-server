package io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.entity.SocialAccountJpaEntity;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.mapper.SocialAccountJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.repository.SocialAccountJpaCommandRepository;
import io.hirecore.hirecorememberserver.modules.account.domain.SocialAccount;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.SaveSocialAccountPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class SocialAccountJpaCommandAdapter implements SaveSocialAccountPort {

    private final SocialAccountJpaEntityMapper socialAccountJpaEntityMapper;
    private final SocialAccountJpaCommandRepository socialAccountJpaCommandRepository;

    @Override
    public SocialAccount save(SocialAccount socialAccount) {
        SocialAccountJpaEntity entity = socialAccountJpaEntityMapper.toJpaEntity(socialAccount);

        Collection<Object> domainEvents = socialAccount.pollAllEvents();
        if (domainEvents != null && !domainEvents.isEmpty()) {
            domainEvents.forEach(entity::recordPersistenceEvent);
        }

        SocialAccountJpaEntity savedEntity = socialAccountJpaCommandRepository.save(entity);
        return socialAccountJpaEntityMapper.toDomain(savedEntity);
    }
}

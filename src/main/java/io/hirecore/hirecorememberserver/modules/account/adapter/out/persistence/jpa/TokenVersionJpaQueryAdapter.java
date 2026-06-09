package io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.repository.MemberAccountJpaQueryRepository;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.LoadTokenVersionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TokenVersionJpaQueryAdapter implements LoadTokenVersionPort {

    private final MemberAccountJpaQueryRepository memberAccountJpaQueryRepository;

    @Override
    public Optional<Integer> findCurrentTokenVersion(Long memberId) {
        return memberAccountJpaQueryRepository.findTokenVersionById(memberId);
    }
}

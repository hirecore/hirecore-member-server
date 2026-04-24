package io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.ValidateTokenVersionPort;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.repository.MemberAccountJpaQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenVersionJpaQueryAdapter implements ValidateTokenVersionPort {

    private final MemberAccountJpaQueryRepository memberAccountJpaQueryRepository;

    @Override
    public boolean isValidTokenVersion(Long memberId, int tokenVersion) {
        return memberAccountJpaQueryRepository.findTokenVersionById(memberId)
                .map(currentVersion -> currentVersion == tokenVersion)
                .orElse(false);
    }
}

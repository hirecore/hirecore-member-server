package io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.mapper.MemberAccountJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.repository.MemberAccountJpaQueryRepository;
import io.hirecore.hirecorememberserver.modules.account.domain.MemberAccount;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.MemberAccountQueryPort;
import io.hirecore.hirecorememberserver.common.adapter.out.persistence.exception.DataConsistencyException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberAccountJpaQueryAdapter implements MemberAccountQueryPort {

    private final MemberAccountJpaQueryRepository memberAccountJpaQueryRepository;
    private final MemberAccountJpaEntityMapper memberAccountJpaEntityMapper;

    @Override
    public MemberAccount findById(Long id) {
        return memberAccountJpaQueryRepository.findById(id)
                .map(memberAccountJpaEntityMapper::toDomain)
                .orElseThrow(() -> new DataConsistencyException(
                        String.format("데이터 정합성 오류: MemberAccount(ID: %d)가 존재하지 않습니다.", id)
                ));
    }

    @Override
    public Optional<MemberAccount> findByEmail(String email) {
        return memberAccountJpaQueryRepository.findByEmail(email)
                .map(memberAccountJpaEntityMapper::toDomain);
    }
}

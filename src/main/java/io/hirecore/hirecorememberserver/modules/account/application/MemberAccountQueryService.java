package io.hirecore.hirecorememberserver.modules.account.application;

import io.hirecore.hirecorememberserver.modules.account.domain.MemberAccount;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.LoadMemberAccountPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberAccountQueryService {
    private final LoadMemberAccountPort memberAccountQueryPort;

    @Transactional(readOnly = true)
    public MemberAccount findById(Long id) {
        return memberAccountQueryPort.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<MemberAccount> findByEmail(String email) {
        return memberAccountQueryPort.findByEmail(email);
    }
}

package io.hirecore.hirecorememberserver.modules.account.application;

import io.hirecore.hirecorememberserver.modules.account.domain.MemberAccount;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.MemberAccountCommandPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberAccountCommandService {
    private final MemberAccountCommandPort memberAccountCommandPort;

    @Transactional
    public MemberAccount saveMemberAccount(MemberAccount memberAccount) {
        return memberAccountCommandPort.save(memberAccount);
    }
}

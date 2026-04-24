package io.hirecore.hirecorememberserver.modules.account.application;

import io.hirecore.hirecorememberserver.modules.account.domain.MemberAccount;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.SaveMemberAccountPort;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.IncrementTokenVersionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberAccountCommandService {
    private final SaveMemberAccountPort saveMemberAccountPort;
    private final IncrementTokenVersionPort incrementTokenVersionPort;

    @Transactional
    public MemberAccount saveMemberAccount(MemberAccount memberAccount) {
        return saveMemberAccountPort.save(memberAccount);
    }

    @Transactional
    public void incrementTokenVersion(Long memberId) {
        incrementTokenVersionPort.incrementTokenVersion(memberId);
    }
}

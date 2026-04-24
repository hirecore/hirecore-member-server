package io.hirecore.hirecorememberserver.modules.account.application.port.out;

import io.hirecore.hirecorememberserver.modules.account.domain.MemberAccount;

public interface SaveMemberAccountPort {
    MemberAccount save(MemberAccount memberAccount);
}

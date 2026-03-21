package io.hirecore.hirecorememberserver.modules.account.application.port.out;

import io.hirecore.hirecorememberserver.modules.account.domain.MemberAccount;

public interface MemberAccountCommandPort {
    MemberAccount save(MemberAccount memberAccount);
}

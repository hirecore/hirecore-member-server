package io.hirecore.hirecorememberserver.modules.account.application.port.out;

import io.hirecore.hirecorememberserver.modules.account.domain.MemberAccount;

import java.util.Optional;

public interface MemberAccountQueryPort {
    MemberAccount findById(Long id);
    Optional<MemberAccount> findByEmail(String email);
}

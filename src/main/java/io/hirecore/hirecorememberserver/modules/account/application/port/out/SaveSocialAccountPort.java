package io.hirecore.hirecorememberserver.modules.account.application.port.out;

import io.hirecore.hirecorememberserver.modules.account.domain.SocialAccount;

public interface SaveSocialAccountPort {
    SocialAccount save(SocialAccount socialAccount);
}

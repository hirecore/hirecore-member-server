package io.hirecore.hirecorememberserver.modules.account.application.port.out;

import io.hirecore.hirecorememberserver.modules.account.application.port.out.dto.result.SocialUserProfileResult;

public interface LoadSocialUserProfilePort {
    SocialUserProfileResult load(String authorizationCode);
}

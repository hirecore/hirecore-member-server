package io.hirecore.hirecorememberserver.modules.account.application.port.out;

import io.hirecore.hirecorememberserver.modules.account.application.port.out.dto.response.SocialUserProfileResult;

public interface LoadSocialUserProfilePort {
    SocialUserProfileResult load(String authorizationCode);
}

package io.hirecore.hirecorememberserver.modules.profile.application.port.out;

import io.hirecore.hirecorememberserver.modules.profile.domain.UserProfileDetail;

public interface SaveUserProfilePort {
    UserProfileDetail save(UserProfileDetail userProfileDetail);
}

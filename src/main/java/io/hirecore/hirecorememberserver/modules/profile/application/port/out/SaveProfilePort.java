package io.hirecore.hirecorememberserver.modules.profile.application.port.out;

import io.hirecore.hirecorememberserver.modules.profile.domain.Profile;

public interface SaveProfilePort {
    Profile save(Profile profile);
}

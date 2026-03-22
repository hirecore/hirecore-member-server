package io.hirecore.hirecorememberserver.modules.profile.application;

import io.hirecore.hirecorememberserver.modules.profile.application.port.out.ProfileCommandPort;
import io.hirecore.hirecorememberserver.modules.profile.domain.Profile;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProfileCommandService {
    private final ProfileCommandPort profileCommandPort;

    public Profile save(Profile profile) {
        return profileCommandPort.save(profile);
    }
}

package io.hirecore.hirecorememberserver.modules.profile.application;

import io.hirecore.hirecorememberserver.modules.profile.application.port.out.ProfileQueryPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProfileQueryService {
    private final ProfileQueryPort profileQueryPort;

    public boolean existsByPublicCode(String code) {
        return profileQueryPort.existsByPublicCode(code);
    }
}

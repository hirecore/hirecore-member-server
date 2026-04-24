package io.hirecore.hirecorememberserver.modules.storage.application;

import io.hirecore.hirecorememberserver.modules.storage.application.port.out.LoadUserUsedQuotaPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserStorageUsageCommandService {
    private final LoadUserUsedQuotaPort loadUserUsedQuotaBytesPort;
}

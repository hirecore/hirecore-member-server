package io.hirecore.hirecorememberserver.modules.storage.adapter.in.shared;

import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.SaveUserStorageUsageLogPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserStorageUsageLogCommandAdapter implements SaveUserStorageUsageLogPort {
}

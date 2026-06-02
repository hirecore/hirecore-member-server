package io.hirecore.hirecorememberserver.sharedkernel.application.port.out;

import java.util.Optional;

public interface LoadImageUrlPort {
    Optional<String> findUrlById(Long imageId);
}

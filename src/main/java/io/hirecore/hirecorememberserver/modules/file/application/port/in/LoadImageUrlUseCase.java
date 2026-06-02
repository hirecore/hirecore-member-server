package io.hirecore.hirecorememberserver.modules.file.application.port.in;

import java.util.Optional;

public interface LoadImageUrlUseCase {
    Optional<String> execute(Long imageId);
}

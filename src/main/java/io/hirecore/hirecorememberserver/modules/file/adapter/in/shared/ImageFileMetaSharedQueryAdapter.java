package io.hirecore.hirecorememberserver.modules.file.adapter.in.shared;

import io.hirecore.hirecorememberserver.modules.file.application.port.in.LoadImageUrlUseCase;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadImageUrlPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ImageFileMetaSharedQueryAdapter implements LoadImageUrlPort {

    private final LoadImageUrlUseCase loadImageUrlUseCase;

    @Override
    public Optional<String> findUrlById(Long imageId) {
        return loadImageUrlUseCase.execute(imageId);
    }
}

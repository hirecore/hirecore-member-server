package io.hirecore.hirecorememberserver.modules.file.adapter.in.shared;

import io.hirecore.hirecorememberserver.modules.file.application.port.in.MarkImageFileMetasAsUploadedUseCase;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.MarkImagesAsUploadedPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class ImageFileMetaSharedCommandAdapter implements MarkImagesAsUploadedPort {

    private final MarkImageFileMetasAsUploadedUseCase markImageFileMetasAsUploadedUseCase;

    @Override
    public void markUploaded(Long memberAccountId, Collection<Long> imageIds) {
        markImageFileMetasAsUploadedUseCase.execute(memberAccountId, imageIds);
    }
}

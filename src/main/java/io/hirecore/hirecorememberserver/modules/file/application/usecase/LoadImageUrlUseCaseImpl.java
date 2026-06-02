package io.hirecore.hirecorememberserver.modules.file.application.usecase;

import io.hirecore.hirecorememberserver.modules.file.application.port.in.LoadImageUrlUseCase;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.LoadImageFileMetaPort;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.ResolveImageObjectUrlPort;
import io.hirecore.hirecorememberserver.modules.file.domain.ImageFileMeta;
import io.hirecore.hirecorememberserver.modules.file.domain.vo.UploadStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoadImageUrlUseCaseImpl implements LoadImageUrlUseCase {

    private final LoadImageFileMetaPort loadImageFileMetaPort;
    private final ResolveImageObjectUrlPort resolveImageObjectUrlPort;

    @Override
    @Transactional(readOnly = true)
    public Optional<String> execute(Long imageId) {
        if (imageId == null) {
            return Optional.empty();
        }
        return loadImageFileMetaPort.findById(imageId)
                .filter(meta -> meta.getUploadStatus() == UploadStatus.UPLOADED)
                .map(ImageFileMeta::getObjectKey)
                .map(resolveImageObjectUrlPort::resolveUrl);
    }
}

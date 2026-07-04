package io.hirecore.hirecorememberserver.modules.file.application.usecase;

import io.hirecore.hirecorememberserver.modules.file.application.port.in.MarkImageFileMetasAsOrphanedUseCase;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.LoadImageFileMetaPort;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.UpdateImageFileMetaPort;
import io.hirecore.hirecorememberserver.modules.file.application.util.ImageFileMetaVerifier;
import io.hirecore.hirecorememberserver.modules.file.domain.ImageFileMeta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MarkImageFileMetasAsOrphanedUseCaseImpl implements MarkImageFileMetasAsOrphanedUseCase {

    private final LoadImageFileMetaPort loadImageFileMetaPort;
    private final UpdateImageFileMetaPort updateImageFileMetaPort;

    @Override
    @Transactional
    public void execute(Long memberAccountId, Collection<Long> imageIds) {
        if (imageIds == null || imageIds.isEmpty()) {
            return;
        }

        List<Long> distinctImageIds = imageIds.stream().distinct().toList();
        List<ImageFileMeta> imageFileMetas = loadImageFileMetaPort.findAllByIds(distinctImageIds);

        ImageFileMetaVerifier.verifyAllExist(distinctImageIds, imageFileMetas);
        ImageFileMetaVerifier.verifyAllOwnedBy(memberAccountId, imageFileMetas);

        // 이미 ORPHANED 는 제외 (멱등), 그 외 부적합 상태는 도메인이 예외
        List<ImageFileMeta> toTransition = imageFileMetas.stream()
                .filter(meta -> !meta.isOrphaned())
                .toList();
        if (toTransition.isEmpty()) {
            return;
        }

        toTransition.forEach(ImageFileMeta::markOrphaned);

        updateImageFileMetaPort.markAllAsOrphaned(toTransition);
    }
}

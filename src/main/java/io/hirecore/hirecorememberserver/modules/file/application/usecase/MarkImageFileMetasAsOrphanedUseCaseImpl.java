package io.hirecore.hirecorememberserver.modules.file.application.usecase;

import io.hirecore.hirecorememberserver.modules.file.application.port.in.MarkImageFileMetasAsOrphanedUseCase;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.LoadImageFileMetaPort;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.UpdateImageFileMetaPort;
import io.hirecore.hirecorememberserver.modules.file.application.util.ImageFileMetaVerifier;
import io.hirecore.hirecorememberserver.modules.file.domain.ImageFileMeta;
import io.hirecore.hirecorememberserver.modules.file.domain.vo.UploadStatus;
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

        // 멱등 처리: 이미 ORPHANED 인 항목은 전이 대상에서 제외합니다 (영속 호출도 생략).
        // 다른 상태(PENDING, DELETED) 면 도메인이 INVALID_UPLOAD_STATUS_TRANSITION 예외를 던집니다.
        List<ImageFileMeta> toTransition = imageFileMetas.stream()
                .filter(meta -> meta.getUploadStatus() != UploadStatus.ORPHANED)
                .toList();
        if (toTransition.isEmpty()) {
            return;
        }

        toTransition.forEach(ImageFileMeta::markOrphaned);

        updateImageFileMetaPort.markAllAsOrphaned(toTransition);
    }
}

package io.hirecore.hirecorememberserver.modules.file.application.usecase;

import io.hirecore.hirecorememberserver.modules.file.application.port.in.MarkImageFileMetasAsUploadedUseCase;
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
public class MarkImageFileMetasAsUploadedUseCaseImpl implements MarkImageFileMetasAsUploadedUseCase {

    private final LoadImageFileMetaPort loadImageFileMetaPort;
    private final UpdateImageFileMetaPort updateImageFileMetaPort;

    @Override
    @Transactional
    public void execute(Long memberAccountId, Collection<Long> imageIds) {
        if (imageIds == null || imageIds.isEmpty()) {
            return;
        }

        List<Long> distinctImageIds = imageIds.stream().distinct().toList();
        List<ImageFileMeta> loadImageFileMetas = loadImageFileMetaPort.findAllByIds(distinctImageIds);

        ImageFileMetaVerifier.verifyAllExist(distinctImageIds, loadImageFileMetas);
        ImageFileMetaVerifier.verifyAllOwnedBy(memberAccountId, loadImageFileMetas);

        // 이미 UPLOADED 는 제외해 재영속 비용 절감 (멱등성은 도메인이 보장)
        List<ImageFileMeta> toTransition = loadImageFileMetas.stream()
                .filter(meta -> !meta.isUploaded())
                .toList();

        if (toTransition.isEmpty()) {
            return;
        }

        // 전이 규칙 검증 + ImageUploadedEvent 누적 (발행은 어댑터 save() 시점)
        toTransition.forEach(ImageFileMeta::markUploaded);

        updateImageFileMetaPort.markAllAsUploaded(toTransition);
    }
}

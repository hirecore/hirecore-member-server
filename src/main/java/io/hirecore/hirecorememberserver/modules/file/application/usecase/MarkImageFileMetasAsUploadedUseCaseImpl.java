package io.hirecore.hirecorememberserver.modules.file.application.usecase;

import io.hirecore.hirecorememberserver.modules.file.application.port.in.MarkImageFileMetasAsUploadedUseCase;
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

        // 이미 UPLOADED 인 항목은 전이 대상에서 제외하여 불필요한 영속/이벤트 발행을 생략합니다.
        // (멱등성 자체는 도메인 markUploaded() 가 보장하며, 여기서는 재영속 비용만 줄입니다.)
        // PENDING 은 전이 대상으로 통과시키고, ORPHANED/DELETED 는 도메인 규칙에 따라 예외가 발생합니다.
        List<ImageFileMeta> toTransition = loadImageFileMetas.stream()
                .filter(meta -> meta.getUploadStatus() != UploadStatus.UPLOADED)
                .toList();

        if (toTransition.isEmpty()) {
            return;
        }

        // 도메인 메서드 호출로 전이 규칙 검증과 ImageUploadedEvent emit이 함께 일어납니다.
        // 누적된 이벤트는 어댑터가 영속 엔티티로 전이한 뒤 Spring Data save()를 통해 발행합니다.
        toTransition.forEach(ImageFileMeta::markUploaded);

        updateImageFileMetaPort.markAllAsUploaded(toTransition);
    }
}

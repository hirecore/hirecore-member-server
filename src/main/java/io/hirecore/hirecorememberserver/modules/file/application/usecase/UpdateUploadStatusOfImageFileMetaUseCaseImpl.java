package io.hirecore.hirecorememberserver.modules.file.application.usecase;

import io.hirecore.hirecorememberserver.modules.file.application.port.in.UpdateUploadStatusOfImageFileMetaUseCase;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.LoadImageFileMetaPort;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.UpdateImageFileMetaPort;
import io.hirecore.hirecorememberserver.modules.file.application.util.ImageFileMetaOwnershipVerifier;
import io.hirecore.hirecorememberserver.modules.file.domain.ImageFileMeta;
import io.hirecore.hirecorememberserver.modules.file.domain.vo.UploadStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdateUploadStatusOfImageFileMetaUseCaseImpl implements UpdateUploadStatusOfImageFileMetaUseCase {

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

        ImageFileMetaOwnershipVerifier.verifyAllExist(distinctImageIds, imageFileMetas);
        ImageFileMetaOwnershipVerifier.verifyAllOwnedBy(memberAccountId, imageFileMetas);

        // 멱등 처리: 이미 UPLOADED 인 항목은 전이 대상에서 제외합니다.
        // PUT 수정 흐름에서 변동 없는 이미지가 재요청되어도 INVALID_UPLOAD_STATUS_TRANSITION 이 나지 않도록 합니다.
        // DELETED 등 다른 상태는 통과시키지 않고 도메인 규칙에 따라 예외가 발생합니다.
        List<ImageFileMeta> toTransition = imageFileMetas.stream()
                .filter(meta -> meta.getUploadStatus() != UploadStatus.UPLOADED)
                .toList();
        if (toTransition.isEmpty()) {
            return;
        }

        // 도메인 메서드 호출로 전이 규칙 검증과 ImageUploadedEvent emit이 함께 일어납니다.
        // 누적된 이벤트는 어댑터가 영속 엔티티로 전이한 뒤 Spring Data save()를 통해 발행합니다.
        toTransition.forEach(meta -> meta.updateUploadStatus(UploadStatus.UPLOADED));

        updateImageFileMetaPort.markAllAsUploaded(toTransition);
    }
}

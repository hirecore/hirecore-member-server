package io.hirecore.hirecorememberserver.modules.file.application.usecase;

import io.hirecore.hirecorememberserver.modules.file.application.exception.FileApplicationException;
import io.hirecore.hirecorememberserver.modules.file.application.exception.FileApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.file.application.port.in.UpdateUploadStatusOfImageFileMetaUseCase;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.LoadImageFileMetaPort;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.UpdateImageFileMetaPort;
import io.hirecore.hirecorememberserver.modules.file.domain.ImageFileMeta;
import io.hirecore.hirecorememberserver.modules.file.domain.vo.UploadStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

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

        verifyAllExist(distinctImageIds, imageFileMetas);
        verifyAllOwnedBy(memberAccountId, imageFileMetas);

        // 도메인 메서드 호출로 전이 규칙 검증과 ImageUploadedEvent emit이 함께 일어납니다.
        // 누적된 이벤트는 어댑터가 영속 엔티티로 전이한 뒤 Spring Data save()를 통해 발행합니다.
        imageFileMetas.forEach(meta -> meta.updateUploadStatus(UploadStatus.UPLOADED));

        updateImageFileMetaPort.markAllAsUploaded(imageFileMetas);
    }

    private static void verifyAllExist(List<Long> requestedIds, List<ImageFileMeta> loadedMetas) {
        if (loadedMetas.size() != requestedIds.size()) {
            throw new FileApplicationException(
                    FileApplicationExceptionCodeCluster.DetailResponse.IMAGE_NOT_FOUND
            );
        }
    }

    private static void verifyAllOwnedBy(Long memberAccountId, List<ImageFileMeta> imageFileMetas) {
        for (ImageFileMeta imageFileMeta : imageFileMetas) {
            if (!Objects.equals(memberAccountId, imageFileMeta.getMemberAccountId())) {
                throw new FileApplicationException(
                        FileApplicationExceptionCodeCluster.DetailResponse.IMAGE_OWNERSHIP_VIOLATION
                );
            }
        }
    }
}

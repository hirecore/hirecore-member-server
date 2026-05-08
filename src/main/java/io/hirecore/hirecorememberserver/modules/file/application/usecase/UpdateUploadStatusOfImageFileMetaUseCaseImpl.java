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
        verifyAllTransitionableToUploaded(imageFileMetas);

        updateImageFileMetaPort.markAllAsUploaded(distinctImageIds);
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

    /**
     * 도메인 메서드를 detached 인스턴스에 호출해 전이 규칙(PENDING → UPLOADED)을 검증합니다.
     * 호출 결과의 mutation은 영속화되지 않으며, 도메인 invariants를 만족하는지 확인하는 용도로만 사용됩니다.
     * 실제 영속 엔티티의 상태 전이는 어댑터에서 별도로 적용됩니다.
     */
    private static void verifyAllTransitionableToUploaded(List<ImageFileMeta> imageFileMetas) {
        for (ImageFileMeta imageFileMeta : imageFileMetas) {
            imageFileMeta.updateUploadStatus(UploadStatus.UPLOADED);
        }
    }
}

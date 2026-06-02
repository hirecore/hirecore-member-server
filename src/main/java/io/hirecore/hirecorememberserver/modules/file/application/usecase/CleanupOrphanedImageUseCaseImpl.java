package io.hirecore.hirecorememberserver.modules.file.application.usecase;

import io.hirecore.hirecorememberserver.modules.file.application.port.in.CleanupOrphanedImageUseCase;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.DeleteObjectsFromStoragePort;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.LoadImageFileMetaPort;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.UpdateImageFileMetaPort;
import io.hirecore.hirecorememberserver.modules.file.application.properties.OrphanedImageCleanupProperties;
import io.hirecore.hirecorememberserver.modules.file.domain.ImageFileMeta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CleanupOrphanedImageUseCaseImpl implements CleanupOrphanedImageUseCase {

    private final LoadImageFileMetaPort loadImageFileMetaPort;
    private final UpdateImageFileMetaPort updateImageFileMetaPort;
    private final DeleteObjectsFromStoragePort deleteObjectsFromStoragePort;
    private final OrphanedImageCleanupProperties properties;

    @Override
    @Transactional
    public int execute() {
        Instant threshold = Instant.now().minus(properties.gracePeriod());
        List<ImageFileMeta> candidates = loadImageFileMetaPort.findOrphanedCandidates(
                threshold,
                properties.batchSize()
        );

        if (candidates.isEmpty()) {
            return 0;
        }

        Map<String, List<ImageFileMeta>> byBucket = candidates.stream()
                .collect(Collectors.groupingBy(ImageFileMeta::getBucketName));

        List<ImageFileMeta> succeeded = new ArrayList<>();
        for (Map.Entry<String, List<ImageFileMeta>> entry : byBucket.entrySet()) {
            String bucket = entry.getKey();
            List<ImageFileMeta> group = entry.getValue();

            Map<String, ImageFileMeta> metaByKey = new LinkedHashMap<>();
            group.forEach(meta -> metaByKey.put(meta.getObjectKey(), meta));

            Set<String> succeededKeys = deleteObjectsFromStoragePort.deleteObjects(bucket, metaByKey.keySet());
            succeededKeys.forEach(key -> succeeded.add(metaByKey.get(key)));
        }

        if (succeeded.isEmpty()) {
            return 0;
        }

        succeeded.forEach(ImageFileMeta::markDeleted);
        updateImageFileMetaPort.markAllAsDeleted(succeeded);

        log.info(
                "ORPHANED 이미지 정리 완료: {} 건 DELETED 마킹 (후보 {} 건 중)",
                succeeded.size(), candidates.size()
        );
        return succeeded.size();
    }
}

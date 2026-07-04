package io.hirecore.hirecorememberserver.modules.file.application.util;

import io.hirecore.hirecorememberserver.modules.file.application.exception.FileApplicationException;
import io.hirecore.hirecorememberserver.modules.file.application.exception.FileApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.file.domain.ImageFileMeta;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// 이미지 메타 컬렉션 존재성/소유권 검증 헬퍼
public final class ImageFileMetaVerifier {

    private ImageFileMetaVerifier() {}

    // 요청 ID 가 모두 로드되었는지 확인, 누락 시 IMAGE_NOT_FOUND
    public static void verifyAllExist(List<Long> requestedIds, List<ImageFileMeta> loadedMetas) {
        Set<Long> loadedIds = loadedMetas.stream()
                .map(ImageFileMeta::getId)
                .collect(Collectors.toSet());

        if (!loadedIds.containsAll(requestedIds)) {
            throw new FileApplicationException(
                    FileApplicationExceptionCodeCluster.DetailResponse.IMAGE_NOT_FOUND
            );
        }
    }

    // 전부 호출자 소유인지 확인, 위반 시 IMAGE_OWNERSHIP_VIOLATION
    public static void verifyAllOwnedBy(Long memberAccountId, List<ImageFileMeta> imageFileMetas) {
        for (ImageFileMeta imageFileMeta : imageFileMetas) {
            if (!imageFileMeta.isOwnedBy(memberAccountId)) {
                throw new FileApplicationException(
                        FileApplicationExceptionCodeCluster.DetailResponse.IMAGE_OWNERSHIP_VIOLATION
                );
            }
        }
    }
}

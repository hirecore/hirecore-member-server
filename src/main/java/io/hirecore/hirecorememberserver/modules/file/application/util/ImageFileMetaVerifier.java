package io.hirecore.hirecorememberserver.modules.file.application.util;

import io.hirecore.hirecorememberserver.modules.file.application.exception.FileApplicationException;
import io.hirecore.hirecorememberserver.modules.file.application.exception.FileApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.file.domain.ImageFileMeta;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 이미지 메타 컬렉션에 대한 존재성 / 소유권 검증 헬퍼.
 *
 * <p>여러 UseCase 가 동일한 검증 단계를 반복하기에 정적 메서드로 추출합니다.</p>
 */
public final class ImageFileMetaVerifier {

    private ImageFileMetaVerifier() {}

    /**
     * 요청한 모든 imageId 가 로드된 메타에 ID 단위로 정확히 매칭됨을 확인합니다.
     * 누락된 항목이 있으면 {@link FileApplicationExceptionCodeCluster.DetailResponse#IMAGE_NOT_FOUND} 예외를 던집니다.
     */
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

    /**
     * 모든 메타가 호출자 소유인지 확인합니다.
     * 하나라도 다른 소유자라면 {@link FileApplicationExceptionCodeCluster.DetailResponse#IMAGE_OWNERSHIP_VIOLATION} 예외를 던집니다.
     */
    public static void verifyAllOwnedBy(Long memberAccountId, List<ImageFileMeta> imageFileMetas) {
        for (ImageFileMeta imageFileMeta : imageFileMetas) {
            if (!Objects.equals(memberAccountId, imageFileMeta.getMemberAccountId())) {
                throw new FileApplicationException(
                        FileApplicationExceptionCodeCluster.DetailResponse.IMAGE_OWNERSHIP_VIOLATION
                );
            }
        }
    }
}

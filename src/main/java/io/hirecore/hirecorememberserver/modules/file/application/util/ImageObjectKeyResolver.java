package io.hirecore.hirecorememberserver.modules.file.application.util;

import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.DomainType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Purpose;

/**
 * 분류 VO 값으로부터 S3 Object Key 의 path segment 를 합성합니다.
 *
 * <p>{@code DomainType} / {@code Purpose} 가 sharedkernel 로 이전되면서, S3 경로 합성이라는
 * file BC 고유 관심사를 분류 VO 자체에서 분리하기 위해 도입한 매핑 함수입니다.</p>
 */
public final class ImageObjectKeyResolver {

    private ImageObjectKeyResolver() {}

    public static String pathSegmentOf(DomainType domainType) {
        return switch (domainType) {
            case PORTFOLIO -> "portfolio";
            case RESUME -> "resume";
        };
    }

    public static String pathSegmentOf(Purpose purpose) {
        return switch (purpose) {
            case CONTENT_IMAGE -> "content-image";
            case THUMBNAIL_IMAGE -> "thumbnail-image";
        };
    }
}

package io.hirecore.hirecorememberserver.modules.file.application.util;

import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.DomainType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Purpose;

// 분류 VO 로부터 S3 Object Key path segment 합성 (file BC 고유 관심사 분리)
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

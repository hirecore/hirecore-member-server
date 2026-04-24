package io.hirecore.hirecorememberserver.modules.file.domain.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Purpose {
    CONTENT_IMAGE("content-image"),
    THUMBNAIL_IMAGE("thumbnail-image");

    private final String pathSegment;
}

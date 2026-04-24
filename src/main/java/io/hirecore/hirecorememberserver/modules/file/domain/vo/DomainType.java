package io.hirecore.hirecorememberserver.modules.file.domain.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DomainType {
    PORTFOLIO("portfolio"),
    RESUME("resume"),
    COVER_LETTER("cover-letter");

    private final String pathSegment;
}
package io.hirecore.hirecorememberserver.modules.storage.domain.vo;

import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.DomainType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Purpose;

public enum ResourceKind {
    PORTFOLIO_CONTENT,
    PORTFOLIO_THUMBNAIL,
    RESUME_CONTENT,
    RESUME_ATTACHMENT;

    // DomainType/Purpose 를 storage BC 자체 분류로 해석 (매핑은 storage BC 책임)
    public static ResourceKind resolve(DomainType domainType, Purpose purpose) {
        return switch (domainType) {
            case PORTFOLIO -> switch (purpose) {
                case CONTENT_IMAGE   -> PORTFOLIO_CONTENT;
                case THUMBNAIL_IMAGE -> PORTFOLIO_THUMBNAIL;
            };
            case RESUME -> switch (purpose) {
                case CONTENT_IMAGE   -> RESUME_CONTENT;
                case THUMBNAIL_IMAGE -> RESUME_ATTACHMENT;
            };
        };
    }
}

package io.hirecore.hirecorememberserver.modules.storage.domain.vo;

import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.DomainType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Purpose;

public enum ResourceKind {
    PORTFOLIO_CONTENT,
    PORTFOLIO_THUMBNAIL,
    RESUME_CONTENT,
    RESUME_ATTACHMENT;

    /**
     * publisher 의 도메인 분류 정보({@link DomainType}, {@link Purpose})를 storage BC 자체 분류로 해석합니다.
     *
     * <p>매핑 규칙은 storage BC 의 책임이며, file BC 의 도메인 정보 변경과 독립적으로 진화합니다.</p>
     */
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

package io.hirecore.hirecorememberserver.modules.portfolio.domain;

import com.github.f4b6a3.tsid.TsidCreator;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioTagDomainException;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioTagDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.AuditingInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PortfolioTag {
    private final Long id;
    private final String name;
    private final String normalizedTag;
    private final Integer sortOrder;
    private final AuditingInfo auditingInfo;

    /**
     * [복원용 빌더]
     * 데이터베이스 등 외부 인프라에서 조회된 데이터를 도메인 객체로 복원할 때만 사용해야 합니다.
     * Application 계층에서의 임의 호출은 ArchUnit 테스트에 의해 차단됩니다.
     */
    @Builder(access = AccessLevel.PUBLIC)
    private PortfolioTag(
            Long id,
            String name,
            String normalizedTag,
            Integer sortOrder,
            AuditingInfo auditingInfo
    ) {
        ensureInvariants(id, name, normalizedTag, sortOrder, auditingInfo);

        this.id = id;
        this.name = name;
        this.normalizedTag = normalizedTag;
        this.sortOrder = sortOrder;
        this.auditingInfo = auditingInfo;
    }

    public static PortfolioTag create(String name, Integer sortOrder) {
        return PortfolioTag.builder()
                .id(TsidCreator.getTsid().toLong())
                .name(name)
                .normalizedTag(normalize(name))
                .sortOrder(sortOrder)
                .auditingInfo(AuditingInfo.create())
                .build();
    }

    private static String normalize(String name) {
        if (name == null) {
            return null;
        }
        return name.trim().toLowerCase().replaceAll("\\s+", "_");
    }

    private static void ensureInvariants(
            Long id,
            String name,
            String normalizedTag,
            Integer sortOrder,
            AuditingInfo auditingInfo
    ) {
        AssertionUtils.notNull(
                id,
                PortfolioTagDomainExceptionCodeCluster.HiddenDetailResponse.ID_MISSING,
                PortfolioTagDomainException::new
        );
        AssertionUtils.notBlank(
                name,
                PortfolioTagDomainExceptionCodeCluster.HiddenDetailResponse.NAME_MISSING,
                PortfolioTagDomainException::new
        );
        AssertionUtils.notBlank(
                normalizedTag,
                PortfolioTagDomainExceptionCodeCluster.HiddenDetailResponse.NORMALIZED_TAG_MISSING,
                PortfolioTagDomainException::new
        );
        AssertionUtils.notNull(
                sortOrder,
                PortfolioTagDomainExceptionCodeCluster.HiddenDetailResponse.SORT_ORDER_MISSING,
                PortfolioTagDomainException::new
        );
        AssertionUtils.isTrue(
                sortOrder >= 0,
                PortfolioTagDomainExceptionCodeCluster.HiddenDetailResponse.SORT_ORDER_NEGATIVE,
                PortfolioTagDomainException::new
        );
        AssertionUtils.notNull(
                auditingInfo,
                SharedKernelExceptionCodeCluster.HiddenDetailResponse.AUDITING_MISSING,
                PortfolioTagDomainException::new
        );
    }
}

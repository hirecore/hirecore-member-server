package io.hirecore.hirecorememberserver.modules.portfolio.domain;

import com.github.f4b6a3.tsid.TsidCreator;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioTagDomainException;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioTagDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.AuditingInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
public class PortfolioTag {
    private final Long id;
    private final String userInputTag;
    private final String normalizedTag;
    private final Boolean isDeleted;
    private final Instant deletedAt;
    private final AuditingInfo auditingInfo;

    @Builder(access = AccessLevel.PRIVATE)
    private PortfolioTag(
            Long id,
            String userInputTag,
            String normalizedTag,
            Boolean isDeleted,
            Instant deletedAt,
            AuditingInfo auditingInfo
    ) {
        ensureInvariants(id, userInputTag, normalizedTag, auditingInfo);

        this.id = id;
        this.userInputTag = userInputTag;
        this.normalizedTag = normalizedTag;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
        this.auditingInfo = auditingInfo;
    }

    public static PortfolioTag create(String userInputTag) {
        return PortfolioTag.builder()
                .id(TsidCreator.getTsid().toLong())
                .userInputTag(userInputTag)
                .normalizedTag(normalize(userInputTag))
                .isDeleted(false)
                .deletedAt(null)
                .auditingInfo(AuditingInfo.create())
                .build();
    }

    private static String normalize(String userInputTag) {
        if (userInputTag == null) {
            return null;
        }
        return userInputTag.trim().toLowerCase().replaceAll("\\s+", "_");
    }

    private static void ensureInvariants(
            Long id,
            String userInputTag,
            String normalizedTag,
            AuditingInfo auditingInfo
    ) {
        AssertionUtils.notNull(
                id,
                PortfolioTagDomainExceptionCodeCluster.HiddenDetailResponse.ID_MISSING,
                PortfolioTagDomainException::new
        );
        AssertionUtils.notBlank(
                userInputTag,
                PortfolioTagDomainExceptionCodeCluster.HiddenDetailResponse.USER_INPUT_TAG_MISSING,
                PortfolioTagDomainException::new
        );
        AssertionUtils.notBlank(
                normalizedTag,
                PortfolioTagDomainExceptionCodeCluster.HiddenDetailResponse.NORMALIZED_TAG_MISSING,
                PortfolioTagDomainException::new
        );
        AssertionUtils.notNull(
                auditingInfo,
                SharedKernelExceptionCodeCluster.HiddenDetailResponse.AUDITING_MISSING,
                PortfolioTagDomainException::new
        );
    }
}

package io.hirecore.hirecorememberserver.modules.portfolio.domain.vo;

import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioDomainException;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioDomainExceptionCodeCluster;


public record ExternalLink (
        String label,
        String url
){
    private final static String URL_REGEX = "^(http|https)://[^\\s/$.?#].[^\\s]*$";

    public ExternalLink {
        boolean labelPresent = label != null && !label.isBlank();
        boolean urlPresent = url != null && !url.isBlank();

        AssertionUtils.isTrue(
                labelPresent == urlPresent,
                PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.EXTERNAL_LINK_PAIR_INCOMPLETE,
                PortfolioDomainException::new);

        if (urlPresent) {
            AssertionUtils.matches(
                    url,
                    URL_REGEX,
                    PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.EXTERNAL_URL_INVALID,
                    PortfolioDomainException::new);
        }
    }
}

package io.hirecore.hirecorememberserver.sharedkernel.domain.vo;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelExceptionCodeCluster.HiddenDetailResponse;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;


public record ExternalLink(
        String label,
        String url
) {
    private final static String URL_REGEX = "^(http|https)://[^\\s/$.?#].[^\\s]*$";

    public ExternalLink {
        boolean labelPresent = label != null && !label.isBlank();
        boolean urlPresent = url != null && !url.isBlank();

        AssertionUtils.isTrue(
                labelPresent == urlPresent,
                HiddenDetailResponse.EXTERNAL_LINK_PAIR_INCOMPLETE,
                SharedKernelException::new);

        if (urlPresent) {
            AssertionUtils.matches(
                    url,
                    URL_REGEX,
                    HiddenDetailResponse.EXTERNAL_URL_INVALID,
                    SharedKernelException::new);
        }
    }
}

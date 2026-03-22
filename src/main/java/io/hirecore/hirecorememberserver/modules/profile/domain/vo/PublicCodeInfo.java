package io.hirecore.hirecorememberserver.modules.profile.domain.vo;

import io.hirecore.hirecorememberserver.common.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.modules.profile.domain.exception.ProfileDomainException;
import io.hirecore.hirecorememberserver.modules.profile.domain.exception.ProfileDomainExceptionCodeCluster;

import java.util.concurrent.ThreadLocalRandom;

public record PublicCodeInfo(
        String publicCode
) {
    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 8;

    public PublicCodeInfo {
        AssertionUtils.notBlank(
                publicCode,
                ProfileDomainExceptionCodeCluster.HiddenDetailResponse.PUBLIC_CODE_VALUE_INVALID,
                ProfileDomainException::new
        );
        AssertionUtils.isTrue(
                publicCode.length() == CODE_LENGTH,
                ProfileDomainExceptionCodeCluster.HiddenDetailResponse.PUBLIC_CODE_VALUE_INVALID,
                ProfileDomainException::new
        );
    }

    // 무작위 publicCode를 생성한다.
    public static PublicCodeInfo generate() {
        StringBuilder builder = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomIndex = ThreadLocalRandom.current().nextInt(CHAR_POOL.length());
            builder.append(CHAR_POOL.charAt(randomIndex));
        }

        return new PublicCodeInfo(builder.toString());
    }
}

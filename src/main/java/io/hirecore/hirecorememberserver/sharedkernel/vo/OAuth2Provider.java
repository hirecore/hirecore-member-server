package io.hirecore.hirecorememberserver.sharedkernel.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.hirecore.hirecorememberserver.common.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.sharedkernel.exception.SharedKernelException;
import io.hirecore.hirecorememberserver.sharedkernel.exception.SharedKernelExceptionCodeCluster.HiddenDetailResponse;
import lombok.Getter;

/**
 * OAuth2 소셜 로그인 제공자 타입을 정의하는 열거형 클래스입니다.
 * <p>시스템에서 허용하는 소셜 로그인 수단을 관리하며, 각 제공자의 한글 명칭을 포함합니다.</p>
 */
@Getter
public enum OAuth2Provider {
    KAKAO("카카오");

    private final String type;

    OAuth2Provider(String type) {
        this.type = type;
    }

    /**
     * JSON 역직렬화 시 호출되는 팩토리 메서드입니다.
     * <p>
     *  입력값의 존재 여부와 시스템 정의 여부를 검증하는 <b>Fail-Fast</b> 전략을 수행합니다.
     * </p>
     *
     * @param provider JSON 데이터에서 전달된 프로바이더 명칭 (ex: "KAKAO")
     * @return 매칭되는 {@link OAuth2Provider} 상수
     * @throws SharedKernelException 입력값이 비어있거나({@code OAUTH2_PROVIDER_MISSING}),
     * 지원하지 않는 프로바이더일 경우({@code OAUTH2_PROVIDER_INVALID}) 발생합니다.
     */
    @JsonCreator
    public static OAuth2Provider from(String provider) {
        AssertionUtils.notBlank(
                provider,
                HiddenDetailResponse.OAUTH2_PROVIDER_MISSING,
                SharedKernelException::new
        );

        try {
            return OAuth2Provider.valueOf(provider.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new SharedKernelException(HiddenDetailResponse.OAUTH2_PROVIDER_INVALID);
        }
    }
}

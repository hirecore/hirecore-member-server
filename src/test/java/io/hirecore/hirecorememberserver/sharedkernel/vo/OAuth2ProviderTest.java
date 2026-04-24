package io.hirecore.hirecorememberserver.sharedkernel.vo;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.OAuth2Provider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("OAuth2Provider 단위 테스트")
class OAuth2ProviderTest {

    @Nested
    @DisplayName("from() 팩토리 메서드 - 성공")
    class FromSuccessTest {

        @Test
        @DisplayName("'KAKAO' 문자열로 OAuth2Provider.KAKAO를 반환한다")
        void should_return_KAKAO_when_input_is_KAKAO() {
            // when
            OAuth2Provider result = OAuth2Provider.from("KAKAO");

            // then
            assertThat(result).isEqualTo(OAuth2Provider.KAKAO);
        }

        @Test
        @DisplayName("소문자 'kakao'도 대소문자 무시하여 KAKAO를 반환한다")
        void should_return_KAKAO_when_input_is_lowercase() {
            // when
            OAuth2Provider result = OAuth2Provider.from("kakao");

            // then
            assertThat(result).isEqualTo(OAuth2Provider.KAKAO);
        }
    }

    @Nested
    @DisplayName("from() 팩토리 메서드 - 불변식 위반")
    class FromInvariantsTest {

        @ParameterizedTest(name = "입력값이 \"{0}\"이면 OAUTH2_PROVIDER_MISSING 에러가 발생한다")
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t"})
        @DisplayName("빈 값이나 null이면 OAUTH2_PROVIDER_MISSING 에러코드로 예외가 발생한다")
        void should_throw_OAUTH2_PROVIDER_MISSING_for_blank_or_null(String input) {
            assertThatThrownBy(() -> OAuth2Provider.from(input))
                    .isInstanceOf(SharedKernelException.class)
                    .extracting("errorCode")
                    .isEqualTo(SharedKernelExceptionCodeCluster.HiddenDetailResponse.OAUTH2_PROVIDER_MISSING.getErrorCode());
        }

        @ParameterizedTest(name = "입력값이 \"{0}\"이면 OAUTH2_PROVIDER_INVALID 에러가 발생한다")
        @ValueSource(strings = {"NAVER", "GOOGLE", "unknown"})
        @DisplayName("지원하지 않는 프로바이더면 OAUTH2_PROVIDER_INVALID 에러코드로 예외가 발생한다")
        void should_throw_OAUTH2_PROVIDER_INVALID_for_unsupported_provider(String input) {
            assertThatThrownBy(() -> OAuth2Provider.from(input))
                    .isInstanceOf(SharedKernelException.class)
                    .extracting("errorCode")
                    .isEqualTo(SharedKernelExceptionCodeCluster.HiddenDetailResponse.OAUTH2_PROVIDER_INVALID.getErrorCode());
        }
    }

    @Nested
    @DisplayName("Enum 속성 검증")
    class EnumPropertyTest {

        @Test
        @DisplayName("KAKAO의 type은 '카카오'이다")
        void should_have_correct_type() {
            assertThat(OAuth2Provider.KAKAO.getType()).isEqualTo("카카오");
        }
    }
}

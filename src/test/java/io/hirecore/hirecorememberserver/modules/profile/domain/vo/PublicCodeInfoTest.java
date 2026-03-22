package io.hirecore.hirecorememberserver.modules.profile.domain.vo;

import io.hirecore.hirecorememberserver.modules.profile.domain.exception.ProfileDomainException;
import io.hirecore.hirecorememberserver.modules.profile.domain.exception.ProfileDomainExceptionCodeCluster;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PublicCodeInfo VO 단위 테스트")
class PublicCodeInfoTest {

    private static final String VALID_CODE = "Ab3Xy9Zq";

    @Nested
    @DisplayName("생성자 불변식 검증")
    class InvariantsTest {

        @Test
        @DisplayName("유효한 8자리 코드로 생성 시 값이 보존된다")
        void should_preserve_value_when_valid_code_given() {
            PublicCodeInfo info = new PublicCodeInfo(VALID_CODE);

            assertThat(info.publicCode()).isEqualTo(VALID_CODE);
        }

        @Test
        @DisplayName("null 코드는 PUBLIC_CODE_INVALID 에러코드로 예외가 발생한다")
        void should_throw_when_public_code_is_null() {
            assertThatThrownBy(() -> new PublicCodeInfo(null))
                    .isInstanceOf(ProfileDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(ProfileDomainExceptionCodeCluster.HiddenDetailResponse.PUBLIC_CODE_VALUE_INVALID.getErrorCode());
        }

        @Test
        @DisplayName("빈 문자열 코드는 PUBLIC_CODE_INVALID 에러코드로 예외가 발생한다")
        void should_throw_when_public_code_is_blank() {
            assertThatThrownBy(() -> new PublicCodeInfo(""))
                    .isInstanceOf(ProfileDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(ProfileDomainExceptionCodeCluster.HiddenDetailResponse.PUBLIC_CODE_VALUE_INVALID.getErrorCode());
        }

        @Test
        @DisplayName("공백만 있는 코드는 PUBLIC_CODE_INVALID 에러코드로 예외가 발생한다")
        void should_throw_when_public_code_is_whitespace() {
            assertThatThrownBy(() -> new PublicCodeInfo("   "))
                    .isInstanceOf(ProfileDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(ProfileDomainExceptionCodeCluster.HiddenDetailResponse.PUBLIC_CODE_VALUE_INVALID.getErrorCode());
        }

        @Test
        @DisplayName("7자리 코드는 PUBLIC_CODE_INVALID 에러코드로 예외가 발생한다")
        void should_throw_when_code_length_is_7() {
            assertThatThrownBy(() -> new PublicCodeInfo("Ab3Xy9Z"))
                    .isInstanceOf(ProfileDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(ProfileDomainExceptionCodeCluster.HiddenDetailResponse.PUBLIC_CODE_VALUE_INVALID.getErrorCode());
        }

        @Test
        @DisplayName("9자리 코드는 PUBLIC_CODE_INVALID 에러코드로 예외가 발생한다")
        void should_throw_when_code_length_is_9() {
            assertThatThrownBy(() -> new PublicCodeInfo("Ab3Xy9Zqr"))
                    .isInstanceOf(ProfileDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(ProfileDomainExceptionCodeCluster.HiddenDetailResponse.PUBLIC_CODE_VALUE_INVALID.getErrorCode());
        }
    }

    @Nested
    @DisplayName("generate() 팩토리 메서드")
    class GenerateTest {

        @RepeatedTest(10)
        @DisplayName("생성된 코드는 항상 8자리이다")
        void should_always_generate_8_char_code() {
            PublicCodeInfo info = PublicCodeInfo.generate();

            assertThat(info.publicCode()).hasSize(8);
        }

        @RepeatedTest(10)
        @DisplayName("생성된 코드는 영숫자만 포함한다")
        void should_contain_only_alphanumeric_characters() {
            PublicCodeInfo info = PublicCodeInfo.generate();

            assertThat(info.publicCode()).matches("[A-Za-z0-9]{8}");
        }

        @Test
        @DisplayName("생성된 PublicCodeInfo는 불변식을 만족한다 — 재생성 없이 바로 사용 가능하다")
        void should_generate_valid_public_code_info() {
            PublicCodeInfo info = PublicCodeInfo.generate();

            // generate()가 반환한 값을 다시 생성자에 넣어도 예외가 발생하지 않는다
            assertThat(new PublicCodeInfo(info.publicCode())).isNotNull();
        }
    }
}

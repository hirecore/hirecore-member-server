package io.hirecore.hirecorememberserver.modules.portfolio.domain;

import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioJobCategoryDomainException;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioJobCategoryDomainExceptionCodeCluster;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PortfolioJobCategory 도메인 단위 테스트")
class PortfolioJobCategoryTest {

    @Nested
    @DisplayName("create() 팩토리 메서드")
    class CreateTest {

        @Test
        @DisplayName("id/jobCategoryId/connectedAt 가 채워진 상태로 생성된다")
        void should_create_with_required_fields() {
            PortfolioJobCategory category = PortfolioJobCategory.create(10L, null);

            assertThat(category.getId()).isNotNull();
            assertThat(category.getJobCategoryId()).isEqualTo(10L);
            assertThat(category.getConnectedAt()).isNotNull();
        }

        @Test
        @DisplayName("userInput을 보존한다")
        void should_preserve_user_input() {
            PortfolioJobCategory category = PortfolioJobCategory.create(10L, "사용자 정의 직무");

            assertThat(category.getUserInput()).isEqualTo("사용자 정의 직무");
        }
    }

    @Nested
    @DisplayName("불변식 (Invariants) 검증")
    class InvariantsTest {

        @Test
        @DisplayName("id가 null이면 ID_MISSING 예외가 발생한다")
        void should_throw_when_id_is_null() {
            assertThatThrownBy(() -> PortfolioJobCategory.builder()
                    .id(null)
                    .jobCategoryId(10L)
                    .connectedAt(Instant.now())
                    .build())
                    .isInstanceOf(PortfolioJobCategoryDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioJobCategoryDomainExceptionCodeCluster.HiddenDetailResponse.ID_MISSING.getErrorCode());
        }

        @Test
        @DisplayName("jobCategoryId가 null이면 JOB_CATEGORY_ID_MISSING 예외가 발생한다")
        void should_throw_when_job_category_id_is_null() {
            assertThatThrownBy(() -> PortfolioJobCategory.builder()
                    .id(1L)
                    .jobCategoryId(null)
                    .connectedAt(Instant.now())
                    .build())
                    .isInstanceOf(PortfolioJobCategoryDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioJobCategoryDomainExceptionCodeCluster.HiddenDetailResponse.JOB_CATEGORY_ID_MISSING.getErrorCode());
        }

        @Test
        @DisplayName("connectedAt이 null이면 CONNECTED_AT_MISSING 예외가 발생한다")
        void should_throw_when_connected_at_is_null() {
            assertThatThrownBy(() -> PortfolioJobCategory.builder()
                    .id(1L)
                    .jobCategoryId(10L)
                    .connectedAt(null)
                    .build())
                    .isInstanceOf(PortfolioJobCategoryDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioJobCategoryDomainExceptionCodeCluster.HiddenDetailResponse.CONNECTED_AT_MISSING.getErrorCode());
        }

        @Test
        @DisplayName("userInput 이 null 이어도 정상 생성된다")
        void should_allow_null_user_input() {
            PortfolioJobCategory category = PortfolioJobCategory.create(10L, null);

            assertThat(category.getUserInput()).isNull();
        }

        @Test
        @DisplayName("userInput 길이가 USER_INPUT_MAX_LENGTH 와 같으면 정상 생성된다")
        void should_allow_user_input_at_boundary() {
            String boundary = "가".repeat(PortfolioJobCategory.USER_INPUT_MAX_LENGTH);

            PortfolioJobCategory category = PortfolioJobCategory.create(10L, boundary);

            assertThat(category.getUserInput()).isEqualTo(boundary);
        }

        @Test
        @DisplayName("userInput 길이가 USER_INPUT_MAX_LENGTH 를 초과하면 USER_INPUT_TOO_LONG 예외가 발생한다")
        void should_throw_when_user_input_exceeds_max_length() {
            String overflow = "가".repeat(PortfolioJobCategory.USER_INPUT_MAX_LENGTH + 1);

            assertThatThrownBy(() -> PortfolioJobCategory.create(10L, overflow))
                    .isInstanceOf(PortfolioJobCategoryDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioJobCategoryDomainExceptionCodeCluster.HiddenDetailResponse.USER_INPUT_TOO_LONG.getErrorCode());
        }
    }
}

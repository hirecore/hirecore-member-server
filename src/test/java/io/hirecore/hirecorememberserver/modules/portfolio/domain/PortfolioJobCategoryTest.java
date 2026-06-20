package io.hirecore.hirecorememberserver.modules.portfolio.domain;

import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioJobCategoryDomainException;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioJobCategoryDomainExceptionCodeCluster;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PortfolioJobCategory 도메인 단위 테스트")
class PortfolioJobCategoryTest {

    private static final Long PORTFOLIO_ID = 854054534448913873L;

    @Nested
    @DisplayName("create() 팩토리 메서드")
    class CreateTest {

        @Test
        @DisplayName("portfolioId/jobCategoryId/connectedAt 가 채워진 상태로 생성된다")
        void should_create_with_required_fields() {
            PortfolioJobCategory category = PortfolioJobCategory.create(PORTFOLIO_ID, 10L, null);

            assertThat(category.getPortfolioId()).isEqualTo(PORTFOLIO_ID);
            assertThat(category.getLeafJobCategoryId()).isEqualTo(10L);
            assertThat(category.getConnectedAt()).isNotNull();
        }

        @Test
        @DisplayName("userInput을 보존한다")
        void should_preserve_user_input() {
            PortfolioJobCategory category = PortfolioJobCategory.create(PORTFOLIO_ID, 10L, "사용자 정의 직무");

            assertThat(category.getUserInput()).isEqualTo("사용자 정의 직무");
        }
    }

    @Nested
    @DisplayName("modify() 갱신 메서드")
    class ModifyTest {

        private static PortfolioJobCategory existing(Long jobCategoryId, String userInput, Instant connectedAt) {
            return PortfolioJobCategory.builder()
                    .portfolioId(PORTFOLIO_ID)
                    .leafJobCategoryId(jobCategoryId)
                    .userInput(userInput)
                    .connectedAt(connectedAt)
                    .build();
        }

        @Test
        @DisplayName("식별자(portfolioId)는 항상 보존된다")
        void should_preserve_portfolio_id() {
            PortfolioJobCategory before = existing(10L, null, Instant.now());

            PortfolioJobCategory after = before.modify(20L, "사용자 정의 직무");

            assertThat(after.getPortfolioId()).isEqualTo(PORTFOLIO_ID);
        }

        @Test
        @DisplayName("jobCategoryId 와 userInput 이 모두 동일하면 connectedAt 을 보존한다")
        void should_keep_connected_at_when_unchanged() {
            Instant past = Instant.now().minus(7, ChronoUnit.DAYS);
            PortfolioJobCategory before = existing(10L, "백엔드", past);

            PortfolioJobCategory after = before.modify(10L, "백엔드");

            assertThat(after.getConnectedAt()).isEqualTo(past);
        }

        @Test
        @DisplayName("jobCategoryId 가 바뀌면 connectedAt 을 현재 시각으로 갱신한다")
        void should_update_connected_at_when_job_category_id_changes() {
            Instant past = Instant.now().minus(7, ChronoUnit.DAYS);
            PortfolioJobCategory before = existing(10L, "백엔드", past);

            PortfolioJobCategory after = before.modify(20L, "백엔드");

            assertThat(after.getConnectedAt()).isAfter(past);
        }

        @Test
        @DisplayName("userInput 만 바뀌어도 connectedAt 을 현재 시각으로 갱신한다")
        void should_update_connected_at_when_user_input_changes() {
            Instant past = Instant.now().minus(7, ChronoUnit.DAYS);
            PortfolioJobCategory before = existing(10L, "백엔드", past);

            PortfolioJobCategory after = before.modify(10L, "프론트엔드");

            assertThat(after.getConnectedAt()).isAfter(past);
        }

        @Test
        @DisplayName("userInput 이 null → 값 으로 바뀌면 connectedAt 을 갱신한다")
        void should_update_connected_at_when_user_input_changes_from_null() {
            Instant past = Instant.now().minus(7, ChronoUnit.DAYS);
            PortfolioJobCategory before = existing(10L, null, past);

            PortfolioJobCategory after = before.modify(10L, "백엔드");

            assertThat(after.getConnectedAt()).isAfter(past);
        }

        @Test
        @DisplayName("새 값(jobCategoryId/userInput)을 반영한다")
        void should_apply_new_values() {
            PortfolioJobCategory before = existing(10L, "백엔드", Instant.now());

            PortfolioJobCategory after = before.modify(20L, "프론트엔드");

            assertThat(after.getLeafJobCategoryId()).isEqualTo(20L);
            assertThat(after.getUserInput()).isEqualTo("프론트엔드");
        }
    }

    @Nested
    @DisplayName("불변식 (Invariants) 검증")
    class InvariantsTest {

        @Test
        @DisplayName("portfolioId가 null이면 PORTFOLIO_ID_MISSING 예외가 발생한다")
        void should_throw_when_portfolio_id_is_null() {
            assertThatThrownBy(() -> PortfolioJobCategory.builder()
                    .portfolioId(null)
                    .leafJobCategoryId(10L)
                    .connectedAt(Instant.now())
                    .build())
                    .isInstanceOf(PortfolioJobCategoryDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioJobCategoryDomainExceptionCodeCluster.HiddenDetailResponse.PORTFOLIO_ID_MISSING.getErrorCode());
        }

        @Test
        @DisplayName("jobCategoryId가 null이면 JOB_CATEGORY_ID_MISSING 예외가 발생한다")
        void should_throw_when_job_category_id_is_null() {
            assertThatThrownBy(() -> PortfolioJobCategory.builder()
                    .portfolioId(PORTFOLIO_ID)
                    .leafJobCategoryId(null)
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
                    .portfolioId(PORTFOLIO_ID)
                    .leafJobCategoryId(10L)
                    .connectedAt(null)
                    .build())
                    .isInstanceOf(PortfolioJobCategoryDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioJobCategoryDomainExceptionCodeCluster.HiddenDetailResponse.CONNECTED_AT_MISSING.getErrorCode());
        }

        @Test
        @DisplayName("userInput 이 null 이어도 정상 생성된다")
        void should_allow_null_user_input() {
            PortfolioJobCategory category = PortfolioJobCategory.create(PORTFOLIO_ID, 10L, null);

            assertThat(category.getUserInput()).isNull();
        }

        @Test
        @DisplayName("userInput 길이가 USER_INPUT_MAX_LENGTH 와 같으면 정상 생성된다")
        void should_allow_user_input_at_boundary() {
            String boundary = "가".repeat(PortfolioJobCategory.USER_INPUT_MAX_LENGTH);

            PortfolioJobCategory category = PortfolioJobCategory.create(PORTFOLIO_ID, 10L, boundary);

            assertThat(category.getUserInput()).isEqualTo(boundary);
        }

        @Test
        @DisplayName("userInput 길이가 USER_INPUT_MAX_LENGTH 를 초과하면 USER_INPUT_TOO_LONG 예외가 발생한다")
        void should_throw_when_user_input_exceeds_max_length() {
            String overflow = "가".repeat(PortfolioJobCategory.USER_INPUT_MAX_LENGTH + 1);

            assertThatThrownBy(() -> PortfolioJobCategory.create(PORTFOLIO_ID, 10L, overflow))
                    .isInstanceOf(PortfolioJobCategoryDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioJobCategoryDomainExceptionCodeCluster.HiddenDetailResponse.USER_INPUT_TOO_LONG.getErrorCode());
        }
    }
}

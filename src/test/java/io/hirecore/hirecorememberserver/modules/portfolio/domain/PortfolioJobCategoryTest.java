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
    @DisplayName("불변식 (Invariants) 검증")
    class InvariantsTest {

        @Test
        @DisplayName("isDeleted=false로 미삭제 상태를 표현할 수 있다")
        void should_allow_not_deleted_state_with_false() {
            PortfolioJobCategory category = PortfolioJobCategory.builder()
                    .id(1L)
                    .jobCategoryId(10L)
                    .isDeleted(false)
                    .deletedAt(null)
                    .connectedAt(Instant.now())
                    .build();

            assertThat(category.getIsDeleted()).isFalse();
            assertThat(category.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("isDeleted가 null이면 IS_DELETED_MISSING 예외가 발생한다")
        void should_throw_when_is_deleted_is_null() {
            assertThatThrownBy(() -> PortfolioJobCategory.builder()
                    .id(1L)
                    .jobCategoryId(10L)
                    .isDeleted(null)
                    .connectedAt(Instant.now())
                    .build())
                    .isInstanceOf(PortfolioJobCategoryDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioJobCategoryDomainExceptionCodeCluster.HiddenDetailResponse.IS_DELETED_MISSING.getErrorCode());
        }

        @Test
        @DisplayName("id가 null이면 ID_MISSING 예외가 발생한다")
        void should_throw_when_id_is_null() {
            assertThatThrownBy(() -> PortfolioJobCategory.builder()
                    .id(null)
                    .jobCategoryId(10L)
                    .isDeleted(false)
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
                    .isDeleted(false)
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
                    .isDeleted(false)
                    .connectedAt(null)
                    .build())
                    .isInstanceOf(PortfolioJobCategoryDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioJobCategoryDomainExceptionCodeCluster.HiddenDetailResponse.CONNECTED_AT_MISSING.getErrorCode());
        }
    }
}

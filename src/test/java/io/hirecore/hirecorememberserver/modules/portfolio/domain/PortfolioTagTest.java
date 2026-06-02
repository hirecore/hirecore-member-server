package io.hirecore.hirecorememberserver.modules.portfolio.domain;

import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioTagDomainException;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioTagDomainExceptionCodeCluster;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PortfolioTag 도메인 단위 테스트")
class PortfolioTagTest {

    @Nested
    @DisplayName("create() 팩토리 메서드")
    class CreateTest {

        @Test
        @DisplayName("입력 태그를 그대로 보존하고 정규화 태그를 생성한다")
        void should_preserve_user_input_and_generate_normalized() {
            PortfolioTag tag = PortfolioTag.create("Full Stack", 0);

            assertThat(tag.getUserInputTag()).isEqualTo("Full Stack");
            assertThat(tag.getNormalizedTag()).isEqualTo("full_stack");
            assertThat(tag.getSortOrder()).isEqualTo(0);
            assertThat(tag.getId()).isNotNull();
        }

        @Test
        @DisplayName("앞뒤 공백을 제거한 뒤 정규화한다")
        void should_trim_before_normalization() {
            PortfolioTag tag = PortfolioTag.create("  React  ", 1);

            assertThat(tag.getNormalizedTag()).isEqualTo("react");
        }

        @Test
        @DisplayName("연속 공백을 단일 언더스코어로 압축한다")
        void should_collapse_whitespaces_to_underscore() {
            PortfolioTag tag = PortfolioTag.create("팀  프로젝트", 2);

            assertThat(tag.getNormalizedTag()).isEqualTo("팀_프로젝트");
        }

        @Test
        @DisplayName("전달된 sortOrder 가 도메인 객체에 그대로 보관된다")
        void should_hold_provided_sort_order() {
            PortfolioTag tag = PortfolioTag.create("Spring", 5);

            assertThat(tag.getSortOrder()).isEqualTo(5);
        }
    }

    @Nested
    @DisplayName("invariant 검증")
    class InvariantTest {

        @Test
        @DisplayName("sortOrder 가 null 이면 SORT_ORDER_MISSING 예외가 발생한다")
        void should_throw_when_sort_order_is_null() {
            assertThatThrownBy(() -> PortfolioTag.create("Spring", null))
                    .isInstanceOf(PortfolioTagDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioTagDomainExceptionCodeCluster.HiddenDetailResponse.SORT_ORDER_MISSING.getErrorCode());
        }

        @Test
        @DisplayName("sortOrder 가 음수면 SORT_ORDER_NEGATIVE 예외가 발생한다")
        void should_throw_when_sort_order_is_negative() {
            assertThatThrownBy(() -> PortfolioTag.create("Spring", -1))
                    .isInstanceOf(PortfolioTagDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioTagDomainExceptionCodeCluster.HiddenDetailResponse.SORT_ORDER_NEGATIVE.getErrorCode());
        }
    }
}

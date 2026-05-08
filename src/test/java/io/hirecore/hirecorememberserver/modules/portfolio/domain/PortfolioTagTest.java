package io.hirecore.hirecorememberserver.modules.portfolio.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PortfolioTag 도메인 단위 테스트")
class PortfolioTagTest {

    @Nested
    @DisplayName("create() 팩토리 메서드")
    class CreateTest {

        @Test
        @DisplayName("입력 태그를 그대로 보존하고 정규화 태그를 생성한다")
        void should_preserve_user_input_and_generate_normalized() {
            PortfolioTag tag = PortfolioTag.create("Full Stack");

            assertThat(tag.getUserInputTag()).isEqualTo("Full Stack");
            assertThat(tag.getNormalizedTag()).isEqualTo("full_stack");
            assertThat(tag.getIsDeleted()).isFalse();
            assertThat(tag.getDeletedAt()).isNull();
            assertThat(tag.getId()).isNotNull();
        }

        @Test
        @DisplayName("앞뒤 공백을 제거한 뒤 정규화한다")
        void should_trim_before_normalization() {
            PortfolioTag tag = PortfolioTag.create("  React  ");

            assertThat(tag.getNormalizedTag()).isEqualTo("react");
        }

        @Test
        @DisplayName("연속 공백을 단일 언더스코어로 압축한다")
        void should_collapse_whitespaces_to_underscore() {
            PortfolioTag tag = PortfolioTag.create("팀  프로젝트");

            assertThat(tag.getNormalizedTag()).isEqualTo("팀_프로젝트");
        }
    }
}

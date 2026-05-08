package io.hirecore.hirecorememberserver.modules.portfolio.domain;

import io.hirecore.hirecorememberserver.modules.portfolio.domain.vo.PortfolioStatus;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.CollaborationType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.ExternalLink;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Portfolio 도메인 단위 테스트")
class PortfolioTest {

    private static Portfolio createValid(String contentHtml, List<String> tags) {
        return Portfolio.create(
                1L,
                10L,
                null,
                100L,
                null,
                null,
                "차세대 취업 사이트 개발 프로젝트",
                "{\"type\":\"doc\"}",
                contentHtml,
                List.of(),
                null,
                tags,
                CollaborationType.TEAM,
                Visibility.PUBLIC
        );
    }

    @Nested
    @DisplayName("create() 팩토리 메서드")
    class CreateTest {

        @Test
        @DisplayName("유효한 입력으로 PUBLISHED 상태의 Portfolio를 생성한다")
        void should_create_portfolio_with_published_status() {
            Portfolio portfolio = createValid("<p>본문 미리보기</p>", List.of("풀스택", "팀"));

            assertThat(portfolio.getId()).isNotNull();
            assertThat(portfolio.getStatus()).isEqualTo(PortfolioStatus.PUBLISHED);
            assertThat(portfolio.getPortfolioJobCategory()).isNotNull();
            assertThat(portfolio.getPortfolioContent().getPortfolioId()).isEqualTo(portfolio.getId());
            assertThat(portfolio.getPortfolioTags()).hasSize(2);
            assertThat(portfolio.getAuditingInfo()).isNotNull();
        }

        @Test
        @DisplayName("HTML 본문에서 태그를 제거하고 첫 500자를 previewSummary로 도출한다")
        void should_derive_preview_summary_from_html() {
            Portfolio portfolio = createValid("<h2>제목</h2><p>본문 내용입니다.</p>", List.of("태그"));

            assertThat(portfolio.getPreviewSummary()).isEqualTo("제목본문 내용입니다.");
        }

        @Test
        @DisplayName("HTML 본문이 500자를 초과하면 500자로 잘라낸다")
        void should_truncate_preview_summary_to_500_chars() {
            String longHtml = "<p>" + "가".repeat(600) + "</p>";

            Portfolio portfolio = createValid(longHtml, List.of());

            assertThat(portfolio.getPreviewSummary()).hasSize(500);
        }

        @Test
        @DisplayName("태그가 null이면 빈 리스트를 보유한다")
        void should_have_empty_tags_when_input_is_null() {
            Portfolio portfolio = createValid("<p>본문</p>", null);

            assertThat(portfolio.getPortfolioTags()).isEmpty();
        }

        @Test
        @DisplayName("externalLinks가 null이면 빈 리스트로 초기화된다")
        void should_have_empty_external_links_when_input_is_null() {
            Portfolio portfolio = Portfolio.create(
                    1L, 10L, null, null, null, null,
                    "title", "{}", "<p>본문</p>",
                    null,
                    null, null,
                    CollaborationType.PERSONAL, Visibility.PRIVATE
            );

            assertThat(portfolio.getExternalLinks()).isEmpty();
        }
    }
}

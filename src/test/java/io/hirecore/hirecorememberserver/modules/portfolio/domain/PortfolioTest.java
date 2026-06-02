package io.hirecore.hirecorememberserver.modules.portfolio.domain;

import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioDomainException;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.vo.PortfolioStatus;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.CollaborationType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.ExternalLink;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Portfolio 도메인 단위 테스트")
class PortfolioTest {

    private static Portfolio createValid(String previewSummary, List<PortfolioTag> tags, List<ExternalLink> externalLinks) {
        return createValid("차세대 취업 사이트 개발 프로젝트", previewSummary, null, tags, externalLinks);
    }

    private static Portfolio createValid(
            String title,
            String previewSummary,
            String privateMemo,
            List<PortfolioTag> tags,
            List<ExternalLink> externalLinks
    ) {
        return Portfolio.create(
                1L,
                100L,
                null,
                null,
                title,
                previewSummary,
                privateMemo,
                10L,
                null,
                "{\"type\":\"doc\"}",
                "<p>본문</p>",
                List.of(),
                externalLinks,
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
            Portfolio portfolio = createValid(
                    "본문 미리보기",
                    List.of(PortfolioTag.create("풀스택", 0), PortfolioTag.create("팀", 1)),
                    List.of()
            );

            assertThat(portfolio.getId()).isNotNull();
            assertThat(portfolio.getStatus()).isEqualTo(PortfolioStatus.PUBLISHED);
            assertThat(portfolio.getPortfolioJobCategory()).isNotNull();
            assertThat(portfolio.getPortfolioContent().getPortfolioId()).isEqualTo(portfolio.getId());
            assertThat(portfolio.getPortfolioTags()).hasSize(2);
            assertThat(portfolio.getAuditingInfo()).isNotNull();
            assertThat(portfolio.getPreviewSummary()).isEqualTo("본문 미리보기");
        }

        @Test
        @DisplayName("외부에서 주입한 portfolioTags를 그대로 보유한다")
        void should_hold_provided_tags() {
            List<PortfolioTag> tags = List.of(PortfolioTag.create("백엔드", 0));

            Portfolio portfolio = createValid("본문", tags, List.of());

            assertThat(portfolio.getPortfolioTags()).isSameAs(tags);
        }

        @Test
        @DisplayName("외부에서 주입한 externalLinks를 그대로 보유한다")
        void should_hold_provided_external_links() {
            List<ExternalLink> links = List.of(new ExternalLink("Repo", "https://github.com/example"));

            Portfolio portfolio = createValid("본문", List.of(), links);

            assertThat(portfolio.getExternalLinks()).isSameAs(links);
        }
    }

    @Nested
    @DisplayName("invariant 검증")
    class InvariantTest {

        @Test
        @DisplayName("previewSummary가 PREVIEW_SUMMARY_MAX_LENGTH를 초과하면 PREVIEW_SUMMARY_TOO_LONG 예외가 발생한다")
        void should_throw_when_preview_summary_exceeds_max_length() {
            String overflow = "가".repeat(Portfolio.PREVIEW_SUMMARY_MAX_LENGTH + 1);

            assertThatThrownBy(() -> createValid(overflow, List.of(), List.of()))
                    .isInstanceOf(PortfolioDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.PREVIEW_SUMMARY_TOO_LONG.getErrorCode());
        }

        @Test
        @DisplayName("previewSummary가 비어있으면 PREVIEW_SUMMARY_MISSING 예외가 발생한다")
        void should_throw_when_preview_summary_is_blank() {
            assertThatThrownBy(() -> createValid("   ", List.of(), List.of()))
                    .isInstanceOf(PortfolioDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.PREVIEW_SUMMARY_MISSING.getErrorCode());
        }

        @Test
        @DisplayName("title이 TITLE_MAX_LENGTH를 초과하면 TITLE_TOO_LONG 예외가 발생한다")
        void should_throw_when_title_exceeds_max_length() {
            String overflowTitle = "가".repeat(Portfolio.TITLE_MAX_LENGTH + 1);

            assertThatThrownBy(() -> createValid(overflowTitle, "본문 미리보기", null, List.of(), List.of()))
                    .isInstanceOf(PortfolioDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.TITLE_TOO_LONG.getErrorCode());
        }

        @Test
        @DisplayName("privateMemo가 PRIVATE_MEMO_MAX_LENGTH를 초과하면 PRIVATE_MEMO_TOO_LONG 예외가 발생한다")
        void should_throw_when_private_memo_exceeds_max_length() {
            String overflowMemo = "가".repeat(Portfolio.PRIVATE_MEMO_MAX_LENGTH + 1);

            assertThatThrownBy(() -> createValid("제목", "본문 미리보기", overflowMemo, List.of(), List.of()))
                    .isInstanceOf(PortfolioDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.PRIVATE_MEMO_TOO_LONG.getErrorCode());
        }

        @Test
        @DisplayName("externalLinks 개수가 EXTERNAL_LINKS_MAX_COUNT를 초과하면 EXTERNAL_LINKS_TOO_MANY 예외가 발생한다")
        void should_throw_when_external_links_exceed_max_count() {
            List<ExternalLink> overflowLinks = java.util.stream.IntStream.range(0, Portfolio.EXTERNAL_LINKS_MAX_COUNT + 1)
                    .mapToObj(i -> new ExternalLink("Repo" + i, "https://example.com/" + i))
                    .toList();

            assertThatThrownBy(() -> createValid("본문 미리보기", List.of(), overflowLinks))
                    .isInstanceOf(PortfolioDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.EXTERNAL_LINKS_TOO_MANY.getErrorCode());
        }

        @Test
        @DisplayName("portfolioTags 개수가 PORTFOLIO_TAGS_MAX_COUNT를 초과하면 PORTFOLIO_TAGS_TOO_MANY 예외가 발생한다")
        void should_throw_when_portfolio_tags_exceed_max_count() {
            List<PortfolioTag> overflowTags = java.util.stream.IntStream.range(0, Portfolio.PORTFOLIO_TAGS_MAX_COUNT + 1)
                    .mapToObj(i -> PortfolioTag.create("태그" + i, i))
                    .toList();

            assertThatThrownBy(() -> createValid("본문 미리보기", overflowTags, List.of()))
                    .isInstanceOf(PortfolioDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.PORTFOLIO_TAGS_TOO_MANY.getErrorCode());
        }
    }
}

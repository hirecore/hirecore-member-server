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
        return Portfolio.create(
                1L,
                10L,
                null,
                100L,
                null,
                null,
                "차세대 취업 사이트 개발 프로젝트",
                previewSummary,
                "{\"type\":\"doc\"}",
                "<p>본문</p>",
                externalLinks,
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
    }
}

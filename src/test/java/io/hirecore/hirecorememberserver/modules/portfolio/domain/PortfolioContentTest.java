package io.hirecore.hirecorememberserver.modules.portfolio.domain;

import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioContentDomainException;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioContentDomainExceptionCodeCluster;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PortfolioContent 도메인 단위 테스트")
class PortfolioContentTest {

    @Nested
    @DisplayName("create() 팩토리 메서드")
    class CreateTest {

        @Test
        @DisplayName("portfolioId·json·html·imageIds가 모두 주어지면 정상 생성된다")
        void should_create_when_all_fields_provided() {
            PortfolioContent content = PortfolioContent.create(1L, "{\"type\":\"doc\"}", "<p>본문</p>", List.of(10L, 20L));

            assertThat(content.getPortfolioId()).isEqualTo(1L);
            assertThat(content.getContentJson()).isEqualTo("{\"type\":\"doc\"}");
            assertThat(content.getContentHtml()).isEqualTo("<p>본문</p>");
            assertThat(content.getImageIds()).containsExactly(10L, 20L);
        }

        @Test
        @DisplayName("imageIds가 비어있어도 정상 생성된다")
        void should_create_when_image_ids_is_empty() {
            PortfolioContent content = PortfolioContent.create(1L, "{}", "<p></p>", List.of());

            assertThat(content.getImageIds()).isEmpty();
        }

        @Test
        @DisplayName("portfolioId가 null이면 PORTFOLIO_ID_MISSING 예외가 발생한다")
        void should_throw_when_portfolio_id_is_null() {
            assertThatThrownBy(() -> PortfolioContent.create(null, "{}", "<p></p>", List.of()))
                    .isInstanceOf(PortfolioContentDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioContentDomainExceptionCodeCluster.HiddenDetailResponse.PORTFOLIO_ID_MISSING.getErrorCode());
        }

        @Test
        @DisplayName("contentJson이 공백이면 CONTENT_JSON_MISSING 예외가 발생한다")
        void should_throw_when_content_json_is_blank() {
            assertThatThrownBy(() -> PortfolioContent.create(1L, "  ", "<p></p>", List.of()))
                    .isInstanceOf(PortfolioContentDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioContentDomainExceptionCodeCluster.HiddenDetailResponse.CONTENT_JSON_MISSING.getErrorCode());
        }

        @Test
        @DisplayName("contentHtml이 공백이면 CONTENT_HTML_MISSING 예외가 발생한다")
        void should_throw_when_content_html_is_blank() {
            assertThatThrownBy(() -> PortfolioContent.create(1L, "{}", "", List.of()))
                    .isInstanceOf(PortfolioContentDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioContentDomainExceptionCodeCluster.HiddenDetailResponse.CONTENT_HTML_MISSING.getErrorCode());
        }

        @Test
        @DisplayName("imageIds가 null이면 IMAGE_IDS_MISSING 예외가 발생한다")
        void should_throw_when_image_ids_is_null() {
            assertThatThrownBy(() -> PortfolioContent.create(1L, "{}", "<p></p>", null))
                    .isInstanceOf(PortfolioContentDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioContentDomainExceptionCodeCluster.HiddenDetailResponse.IMAGE_IDS_MISSING.getErrorCode());
        }

        @Test
        @DisplayName("imageIds에 null 요소가 섞이면 IMAGE_IDS_CONTAINS_NULL 예외가 발생한다")
        void should_throw_when_image_ids_contains_null_element() {
            assertThatThrownBy(() -> PortfolioContent.create(1L, "{}", "<p></p>", Arrays.asList(10L, null, 20L)))
                    .isInstanceOf(PortfolioContentDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioContentDomainExceptionCodeCluster.HiddenDetailResponse.IMAGE_IDS_CONTAINS_NULL.getErrorCode());
        }
    }
}

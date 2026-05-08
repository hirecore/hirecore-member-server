package io.hirecore.hirecorememberserver.sharedkernel.domain.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HtmlPreviewSummarizer 단위 테스트")
class HtmlPreviewSummarizerTest {

    @Nested
    @DisplayName("정상 변환")
    class HappyPathTest {

        @Test
        @DisplayName("HTML 태그를 제거하고 본문 텍스트만 반환한다")
        void should_strip_html_tags() {
            String result = HtmlPreviewSummarizer.summarize("<p>안녕하세요</p>", 100);
            assertThat(result).isEqualTo("안녕하세요");
        }

        @Test
        @DisplayName("연속 공백/개행을 단일 공백으로 정규화한다")
        void should_normalize_whitespace() {
            String result = HtmlPreviewSummarizer.summarize("<p>첫째\n\n\t둘째   셋째</p>", 100);
            assertThat(result).isEqualTo("첫째 둘째 셋째");
        }

        @Test
        @DisplayName("앞뒤 공백을 trim 한다")
        void should_trim_leading_trailing_whitespace() {
            String result = HtmlPreviewSummarizer.summarize("  <p>본문</p>  ", 100);
            assertThat(result).isEqualTo("본문");
        }

        @Test
        @DisplayName("중첩된 HTML 태그도 모두 제거한다")
        void should_strip_nested_tags() {
            String result = HtmlPreviewSummarizer.summarize("<div><strong><em>강조</em></strong></div>", 100);
            assertThat(result).isEqualTo("강조");
        }
    }

    @Nested
    @DisplayName("길이 제한")
    class LengthLimitTest {

        @Test
        @DisplayName("정규화된 길이가 maxLength 를 초과하면 잘라낸다")
        void should_truncate_when_exceeding_max_length() {
            String result = HtmlPreviewSummarizer.summarize("<p>0123456789</p>", 5);
            assertThat(result).isEqualTo("01234");
        }

        @Test
        @DisplayName("정규화된 길이가 maxLength 와 같으면 그대로 반환한다")
        void should_return_as_is_when_equal_max_length() {
            String result = HtmlPreviewSummarizer.summarize("<p>01234</p>", 5);
            assertThat(result).isEqualTo("01234");
        }
    }

    @Nested
    @DisplayName("빈 입력 처리")
    class EmptyInputTest {

        @Test
        @DisplayName("null 입력은 빈 문자열을 반환한다")
        void should_return_empty_when_null() {
            assertThat(HtmlPreviewSummarizer.summarize(null, 100)).isEmpty();
        }

        @Test
        @DisplayName("blank 입력은 빈 문자열을 반환한다")
        void should_return_empty_when_blank() {
            assertThat(HtmlPreviewSummarizer.summarize("   ", 100)).isEmpty();
        }

        @Test
        @DisplayName("HTML 태그만 있고 본문이 없으면 빈 문자열을 반환한다")
        void should_return_empty_when_only_tags() {
            assertThat(HtmlPreviewSummarizer.summarize("<p></p><br/>", 100)).isEmpty();
        }
    }
}

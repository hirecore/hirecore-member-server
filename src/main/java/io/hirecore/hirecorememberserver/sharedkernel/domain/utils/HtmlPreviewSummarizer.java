package io.hirecore.hirecorememberserver.sharedkernel.domain.utils;

/**
 * HTML 콘텐츠로부터 미리보기 요약 텍스트를 산출하는 유틸리티입니다.
 *
 * <p>HTML 태그 제거 + 연속 공백 정규화 + {@code maxLength} 길이 잘라내기를 수행합니다.
 * BC 별 요약 길이 정책이 다를 수 있으므로 {@code maxLength} 는 호출 측에서 지정합니다.</p>
 */
public final class HtmlPreviewSummarizer {

    private HtmlPreviewSummarizer() {}

    public static String summarize(String contentHtml, int maxLength) {
        if (contentHtml == null || contentHtml.isBlank()) {
            return "";
        }
        String stripped = contentHtml
                .replaceAll("<[^>]*>", "")
                .replaceAll("\\s+", " ")
                .trim();
        return stripped.length() > maxLength
                ? stripped.substring(0, maxLength)
                : stripped;
    }
}

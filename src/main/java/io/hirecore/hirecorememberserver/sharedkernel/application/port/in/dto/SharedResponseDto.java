package io.hirecore.hirecorememberserver.sharedkernel.application.port.in.dto;

/**
 * 여러 BC 의 application 측 Response 출력에서 공유되는 sub-record 컨테이너.
 *
 * <p>BC 별 UseCase 의 Response 안에 nested 로 들어가는 가벼운 데이터 캐리어이다.</p>
 */
public class SharedResponseDto {

    private SharedResponseDto() {}

    /**
     * 에디터 본문 (직렬화된 JSON 문자열 + 렌더된 HTML).
     *
     * <p>application 계층은 도메인에 저장된 직렬화 JSON 문자열을 그대로 노출한다.</p>
     */
    public record RichTextContent(
            String json,
            String html
    ) {}

    /** 사용자 입력 태그 (표시 순서 보유). */
    public record SequentialTag(
            String name,
            Integer sortOrder
    ) {}

    /** 직군 카테고리 계층의 한 원소. */
    public record JobCategory(
            Long id,
            Long depth,
            String categoryCode,
            String name
    ) {}

    /**
     * 썸네일 이미지 (식별자 + 공개 URL).
     *
     * <p>썸네일 미등록 시 본 객체 자체가 {@code null}.
     * 등록은 됐으나 URL 해소 실패 시 {@code imageId} 만 채워지고 {@code imageUrl} 은 {@code null}.</p>
     */
    public record Thumbnail(
            Long imageId,
            String imageUrl
    ) {}

    /** 외부 링크 (라벨 + URL). */
    public record ExternalLink(
            String label,
            String url
    ) {}
}

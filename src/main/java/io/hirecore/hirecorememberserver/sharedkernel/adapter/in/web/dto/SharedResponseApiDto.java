package io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto;

import io.hirecore.hirecorememberserver.common.web.json.TsidId;

/**
 * 여러 BC / 여러 endpoint 의 응답에서 공유되는 sub-record 컨테이너.
 *
 * <p>여기에 모인 record 들은 Portfolio / Resume / CoverLetter 등 도메인을 가로질러
 * 동일 모양으로 재사용된다. endpoint 컨테이너에서 {@code SharedResponseApiDto.RichTextContent}
 * 같은 형태로 참조한다.</p>
 */
public class SharedResponseApiDto {

    private SharedResponseApiDto() {}

    /** 에디터 본문 (직렬화 JSON + 렌더된 HTML). 본문이 필요한 모든 도메인에서 사용. */
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
            @TsidId Long id,
            Long depth,
            String categoryCode,
            String name
    ) {}

    /**
     * 썸네일 이미지 (식별자 + 공개 URL).
     *
     * <p>썸네일 미등록 시 본 객체 자체가 {@code null} 로 응답된다.
     * 등록은 됐으나 URL 해소 실패 (ORPHANED / DELETED) 인 경우 imageId 만 채워지고 imageUrl 은 null.</p>
     */
    public record Thumbnail(
            @TsidId Long imageId,
            String imageUrl
    ) {}

    /** 외부 링크 (라벨 + URL). */
    public record ExternalLink(
            String label,
            String url
    ) {}
}

package io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.Map;

/**
 * 여러 BC / 여러 endpoint 의 요청에서 공유되는 sub-record 컨테이너.
 *
 * <p>여기에 모인 record 들은 Portfolio / Resume / CoverLetter 등 도메인을 가로질러
 * 동일 모양으로 재사용된다. endpoint 컨테이너에서 {@code SharedRequestApiDto.RichTextContent}
 * 같은 형태로 참조한다.</p>
 */
public class SharedRequestApiDto {

    private SharedRequestApiDto() {}

    /** 에디터 본문 입력 (JSON 구조 + HTML). 본문이 필요한 모든 도메인에서 사용. */
    public record RichTextContent(
            @NotNull(message = "본문 JSON은 필수입니다.")
            Map<String, Object> json,

            @NotBlank(message = "본문 HTML은 필수입니다.")
            String html
    ) {}

    /** 사용자 입력 태그 (표시 순서 필수). */
    public record SequentialTag(
            @NotBlank(message = "태그 입력값은 필수입니다.")
            String name,

            @NotNull(message = "태그 정렬 순서는 필수입니다.")
            @PositiveOrZero(message = "태그 정렬 순서는 0 이상이어야 합니다.")
            Integer sortOrder
    ) {}

    /** 직군 카테고리 선택 입력 (식별 코드 + 선택적 라벨). */
    public record JobCategory(
            @NotBlank(message = "직무 카테고리 코드는 필수입니다.")
            String code,

            @Size(max = 10, message = "사용자 입력 라벨은 10자를 초과할 수 없습니다.")
            String userInput
    ) {}

    /** 외부 링크 입력 (라벨 + URL). */
    public record ExternalLink(
            @NotBlank(message = "외부 링크 라벨은 필수입니다.")
            String label,

            @NotBlank(message = "외부 링크 URL은 필수입니다.")
            @Pattern(regexp = "^https?://.+", message = "외부 링크 URL은 http:// 또는 https:// 로 시작해야 합니다.")
            String url
    ) {}
}

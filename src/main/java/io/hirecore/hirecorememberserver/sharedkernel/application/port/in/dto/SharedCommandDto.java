package io.hirecore.hirecorememberserver.sharedkernel.application.port.in.dto;

import java.util.Map;

/**
 * 여러 BC 의 application 측 Command 입력에서 공유되는 sub-record 컨테이너.
 *
 * <p>BC 별 UseCase 의 Command 안에 nested 로 들어가는 가벼운 데이터 캐리어이다.
 * 검증은 상위 Command 의 compact constructor 또는 web 의 Jakarta Validation 에 위임한다.</p>
 */
public class SharedCommandDto {

    private SharedCommandDto() {}

    /** 직군 카테고리 입력 (식별 코드 + 선택적 사용자 라벨). */
    public record JobCategory(
            String code,
            String userInput
    ) {}

    /**
     * 에디터 본문 입력 (직렬화 전 Map 구조 + 렌더된 HTML).
     *
     * <p>application 계층은 Map 을 받아 UseCase 가 String 으로 직렬화한 뒤 도메인으로 전달한다.</p>
     */
    public record RichTextContent(
            Map<String, Object> json,
            String html
    ) {}

    /** 사용자 입력 태그 (표시 순서 보유). */
    public record SequentialTag(
            String name,
            Integer sortOrder
    ) {}

    /** 외부 링크 입력 (라벨 + URL). */
    public record ExternalLink(
            String label,
            String url
    ) {}
}

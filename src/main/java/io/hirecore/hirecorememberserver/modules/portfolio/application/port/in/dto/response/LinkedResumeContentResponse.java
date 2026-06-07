package io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response;

/**
 * 포트폴리오 상세에 연결된 이력서의 메타와 본문.
 *
 * <p>{@code content} 는 자원 자체의 가시성 정책에 따라 결정된다.
 * 자원이 PUBLIC 이거나 viewer 가 자원 소유자이면 본문이 채워지고, 그 외에는 {@code null}.</p>
 */
public record LinkedResumeContentResponse(
        Long id,
        String title,
        PortfolioContentResponse content
) {
}

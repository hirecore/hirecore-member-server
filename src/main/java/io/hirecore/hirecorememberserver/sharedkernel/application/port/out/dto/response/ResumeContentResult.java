package io.hirecore.hirecorememberserver.sharedkernel.application.port.out.dto.response;

import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;

/**
 * 다른 BC 가 이력서의 본문/메타데이터를 한 번에 합성하기 위해 받는 결과.
 *
 * <p>{@code memberAccountId} 와 {@code visibility} 를 함께 노출해 소비자가
 * "자원 자체의 가시성 + viewer 일치 여부" 기반으로 본문 노출 정책을 적용할 수 있도록 한다.</p>
 */
public record ResumeContentResult(
        Long id,
        String title,
        Long memberAccountId,
        Visibility visibility,
        String contentJson,
        String contentHtml
) {
}

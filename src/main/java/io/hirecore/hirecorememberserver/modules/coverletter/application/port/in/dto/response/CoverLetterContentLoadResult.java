package io.hirecore.hirecorememberserver.modules.coverletter.application.port.in.dto.response;

import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;

/**
 * 자기소개서 BC 내부에서 단건 본문 조회 결과를 표현하는 application 응답 DTO.
 *
 * <p>BC 내부 UseCase 의 반환 타입으로 사용되며, in/shared 어댑터에서 sharedkernel
 * {@code CoverLetterContentResult} 로 매핑된다.</p>
 */
public record CoverLetterContentLoadResult(
        Long id,
        String title,
        Long memberAccountId,
        Visibility visibility,
        String contentJson,
        String contentHtml
) {
}

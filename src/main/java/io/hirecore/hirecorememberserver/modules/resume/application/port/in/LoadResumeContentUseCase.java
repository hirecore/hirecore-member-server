package io.hirecore.hirecorememberserver.modules.resume.application.port.in;

import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;

import java.util.Optional;

public interface LoadResumeContentUseCase {

    Optional<Response> execute(Long resumeId);

    /**
     * 이력서 BC 내부 단건 본문 조회 결과.
     *
     * <p>BC 내부 UseCase / 출력 포트의 반환 타입으로 사용되며,
     * in/shared 어댑터에서 sharedkernel {@code ResumeContentResult} 로 매핑된다.</p>
     */
    record Response(
            Long id,
            String title,
            Long memberAccountId,
            Visibility visibility,
            String contentJson,
            String contentHtml
    ) {}
}

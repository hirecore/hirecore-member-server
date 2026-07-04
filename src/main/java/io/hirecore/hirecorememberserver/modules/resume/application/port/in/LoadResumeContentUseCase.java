package io.hirecore.hirecorememberserver.modules.resume.application.port.in;

import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;

import java.util.Optional;

public interface LoadResumeContentUseCase {

    Optional<Response> execute(Long resumeId);

    // BC 내부 단건 본문 조회 결과
    record Response(
            Long id,
            String title,
            Long memberAccountId,
            Visibility visibility,
            String contentJson,
            String contentHtml
    ) {}
}

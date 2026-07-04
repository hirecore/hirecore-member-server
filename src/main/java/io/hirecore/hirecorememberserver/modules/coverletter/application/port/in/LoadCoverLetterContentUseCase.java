package io.hirecore.hirecorememberserver.modules.coverletter.application.port.in;

import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;

import java.util.Optional;

public interface LoadCoverLetterContentUseCase {

    Optional<Response> execute(Long coverLetterId);

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

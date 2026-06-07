package io.hirecore.hirecorememberserver.modules.coverletter.application.port.in;

import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;

import java.util.Optional;

public interface LoadCoverLetterContentUseCase {

    Optional<Response> execute(Long coverLetterId);

    /**
     * 자기소개서 BC 내부 단건 본문 조회 결과.
     *
     * <p>BC 내부 UseCase / 출력 포트의 반환 타입으로 사용되며,
     * in/shared 어댑터에서 sharedkernel {@code CoverLetterContentResult} 로 매핑된다.</p>
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

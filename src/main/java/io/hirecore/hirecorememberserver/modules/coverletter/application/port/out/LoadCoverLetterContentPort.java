package io.hirecore.hirecorememberserver.modules.coverletter.application.port.out;

import io.hirecore.hirecorememberserver.modules.coverletter.application.port.in.dto.response.CoverLetterContentLoadResult;

import java.util.Optional;

public interface LoadCoverLetterContentPort {

    /**
     * 식별자로 자기소개서의 메타와 본문을 한 번에 적재한다.
     * 자원이 존재하지 않거나 본문이 비어 있는 경우 {@link Optional#empty()} 를 반환한다.
     */
    Optional<CoverLetterContentLoadResult> findById(Long coverLetterId);
}

package io.hirecore.hirecorememberserver.modules.coverletter.application.port.out;

import io.hirecore.hirecorememberserver.modules.coverletter.application.port.in.LoadCoverLetterContentUseCase;

import java.util.Optional;

public interface LoadCoverLetterContentPort {

    // 메타 + 본문 단건 적재 (없거나 본문 비면 empty)
    Optional<LoadCoverLetterContentUseCase.Response> findById(Long coverLetterId);
}

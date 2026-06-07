package io.hirecore.hirecorememberserver.modules.coverletter.application.port.in;

import io.hirecore.hirecorememberserver.modules.coverletter.application.port.in.dto.response.CoverLetterContentLoadResult;

import java.util.Optional;

public interface LoadCoverLetterContentUseCase {
    Optional<CoverLetterContentLoadResult> execute(Long coverLetterId);
}

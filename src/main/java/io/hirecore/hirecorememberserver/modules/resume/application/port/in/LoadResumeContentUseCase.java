package io.hirecore.hirecorememberserver.modules.resume.application.port.in;

import io.hirecore.hirecorememberserver.modules.resume.application.port.in.dto.response.ResumeContentLoadResult;

import java.util.Optional;

public interface LoadResumeContentUseCase {
    Optional<ResumeContentLoadResult> execute(Long resumeId);
}

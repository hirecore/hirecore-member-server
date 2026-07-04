package io.hirecore.hirecorememberserver.modules.resume.application.port.out;

import io.hirecore.hirecorememberserver.modules.resume.application.port.in.LoadResumeContentUseCase;

import java.util.Optional;

public interface LoadResumeContentPort {

    // 메타 + 본문 단건 적재 (없거나 본문 비면 empty)
    Optional<LoadResumeContentUseCase.Response> findById(Long resumeId);
}

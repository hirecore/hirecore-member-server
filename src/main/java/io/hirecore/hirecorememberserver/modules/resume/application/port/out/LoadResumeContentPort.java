package io.hirecore.hirecorememberserver.modules.resume.application.port.out;

import io.hirecore.hirecorememberserver.modules.resume.application.port.in.dto.response.ResumeContentLoadResult;

import java.util.Optional;

public interface LoadResumeContentPort {

    /**
     * 식별자로 이력서의 메타와 본문을 한 번에 적재한다.
     * 자원이 존재하지 않거나 본문이 비어 있는 경우 {@link Optional#empty()} 를 반환한다.
     */
    Optional<ResumeContentLoadResult> findById(Long resumeId);
}

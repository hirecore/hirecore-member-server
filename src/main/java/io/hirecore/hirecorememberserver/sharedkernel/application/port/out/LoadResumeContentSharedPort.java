package io.hirecore.hirecorememberserver.sharedkernel.application.port.out;

import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.dto.response.ResumeContentResult;

import java.util.Optional;

public interface LoadResumeContentSharedPort {

    /**
     * 식별자로 이력서의 메타와 본문을 한 번에 적재한다.
     * 자원이 존재하지 않거나 삭제된 경우 {@link Optional#empty()} 를 반환한다.
     */
    Optional<ResumeContentResult> findById(Long resumeId);
}

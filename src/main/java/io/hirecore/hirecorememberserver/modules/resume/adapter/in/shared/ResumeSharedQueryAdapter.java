package io.hirecore.hirecorememberserver.modules.resume.adapter.in.shared;

import io.hirecore.hirecorememberserver.modules.resume.application.port.in.LoadResumeContentUseCase;
import io.hirecore.hirecorememberserver.modules.resume.application.port.in.LoadResumeTitlesUseCase;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadResumeContentSharedPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadResumeTitleSharedPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.dto.response.ResumeContentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

// 타 BC 의 이력서 조회용 sharedkernel out port 단일 구현
@Component
@RequiredArgsConstructor
public class ResumeSharedQueryAdapter implements
        LoadResumeContentSharedPort,
        LoadResumeTitleSharedPort
{

    private final LoadResumeContentUseCase loadResumeContentUseCase;
    private final LoadResumeTitlesUseCase loadResumeTitlesUseCase;

    @Override
    public Optional<ResumeContentResult> findById(Long resumeId) {
        return loadResumeContentUseCase.execute(resumeId)
                .map(result -> new ResumeContentResult(
                        result.id(),
                        result.title(),
                        result.memberAccountId(),
                        result.visibility(),
                        result.contentJson(),
                        result.contentHtml()
                ));
    }

    @Override
    public Map<Long, String> findTitleMapByIds(Collection<Long> resumeIds) {
        return loadResumeTitlesUseCase.execute(resumeIds);
    }
}

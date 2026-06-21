package io.hirecore.hirecorememberserver.modules.resume.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.resume.adapter.out.persistence.jpa.repository.ResumeJpaQueryRepository;
import io.hirecore.hirecorememberserver.modules.resume.application.port.in.LoadResumeContentUseCase;
import io.hirecore.hirecorememberserver.modules.resume.application.port.out.LoadResumeContentPort;
import io.hirecore.hirecorememberserver.modules.resume.application.port.out.LoadResumeTitlePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ResumeJpaQueryAdapter implements
        LoadResumeTitlePort,
        LoadResumeContentPort
{

    private final ResumeJpaQueryRepository resumeJpaQueryRepository;

    @Override
    public Map<Long, String> findTitleMapByIds(Collection<Long> resumeIds) {
        if (resumeIds == null || resumeIds.isEmpty()) {
            return Map.of();
        }
        return resumeJpaQueryRepository.findTitleProjectionsByIds(resumeIds).stream()
                .collect(Collectors.toMap(
                        ResumeJpaQueryRepository.ResumeTitleProjection::getId,
                        ResumeJpaQueryRepository.ResumeTitleProjection::getTitle
                ));
    }

    @Override
    public Optional<LoadResumeContentUseCase.Response> findById(Long resumeId) {
        return resumeJpaQueryRepository.findContentProjectionById(resumeId)
                .map(projection -> new LoadResumeContentUseCase.Response(
                        projection.getId(),
                        projection.getTitle(),
                        projection.getMemberAccountId(),
                        projection.getVisibility(),
                        projection.getContentJson(),
                        projection.getContentHtml()
                ));
    }
}

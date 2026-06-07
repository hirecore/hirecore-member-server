package io.hirecore.hirecorememberserver.modules.resume.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.resume.adapter.out.persistence.jpa.repository.ResumeJpaQueryRepository;
import io.hirecore.hirecorememberserver.modules.resume.application.port.in.dto.response.ResumeContentLoadResult;
import io.hirecore.hirecorememberserver.modules.resume.application.port.out.LoadResumeContentPort;
import io.hirecore.hirecorememberserver.modules.resume.application.port.out.LoadResumeTitlesPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ResumeJpaQueryAdapter implements
        LoadResumeTitlesPort,
        LoadResumeContentPort
{

    private final ResumeJpaQueryRepository resumeJpaQueryRepository;

    @Override
    public Map<Long, String> findAllTitlesByIds(Collection<Long> resumeIds) {
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
    public Optional<ResumeContentLoadResult> findById(Long resumeId) {
        return resumeJpaQueryRepository.findContentProjectionById(resumeId)
                .map(projection -> new ResumeContentLoadResult(
                        projection.getId(),
                        projection.getTitle(),
                        projection.getMemberAccountId(),
                        projection.getVisibility(),
                        projection.getContentJson(),
                        projection.getContentHtml()
                ));
    }
}

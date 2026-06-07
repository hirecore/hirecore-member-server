package io.hirecore.hirecorememberserver.modules.resume.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.resume.adapter.out.persistence.jpa.repository.ResumeJpaQueryRepository;
import io.hirecore.hirecorememberserver.modules.resume.application.port.out.LoadResumeTitlesPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ResumeJpaQueryAdapter implements LoadResumeTitlesPort {

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
}

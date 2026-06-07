package io.hirecore.hirecorememberserver.modules.coverletter.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.coverletter.adapter.out.persistence.jpa.repository.CoverLetterJpaQueryRepository;
import io.hirecore.hirecorememberserver.modules.coverletter.application.port.out.LoadCoverLetterTitlesPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CoverLetterJpaQueryAdapter implements LoadCoverLetterTitlesPort {

    private final CoverLetterJpaQueryRepository coverLetterJpaQueryRepository;

    @Override
    public Map<Long, String> findAllTitlesByIds(Collection<Long> coverLetterIds) {
        if (coverLetterIds == null || coverLetterIds.isEmpty()) {
            return Map.of();
        }
        return coverLetterJpaQueryRepository.findTitleProjectionsByIds(coverLetterIds).stream()
                .collect(Collectors.toMap(
                        CoverLetterJpaQueryRepository.CoverLetterTitleProjection::getId,
                        CoverLetterJpaQueryRepository.CoverLetterTitleProjection::getTitle
                ));
    }
}

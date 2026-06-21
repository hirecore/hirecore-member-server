package io.hirecore.hirecorememberserver.modules.coverletter.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.coverletter.adapter.out.persistence.jpa.repository.CoverLetterJpaQueryRepository;
import io.hirecore.hirecorememberserver.modules.coverletter.application.port.in.LoadCoverLetterContentUseCase;
import io.hirecore.hirecorememberserver.modules.coverletter.application.port.out.LoadCoverLetterContentPort;
import io.hirecore.hirecorememberserver.modules.coverletter.application.port.out.LoadCoverLetterTitlePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CoverLetterJpaQueryAdapter implements
        LoadCoverLetterTitlePort,
        LoadCoverLetterContentPort
{

    private final CoverLetterJpaQueryRepository coverLetterJpaQueryRepository;

    @Override
    public Map<Long, String> findTitleMapByIds(Collection<Long> coverLetterIds) {
        if (coverLetterIds == null || coverLetterIds.isEmpty()) {
            return Map.of();
        }
        return coverLetterJpaQueryRepository.findTitleProjectionsByIds(coverLetterIds).stream()
                .collect(Collectors.toMap(
                        CoverLetterJpaQueryRepository.CoverLetterTitleProjection::getId,
                        CoverLetterJpaQueryRepository.CoverLetterTitleProjection::getTitle
                ));
    }

    @Override
    public Optional<LoadCoverLetterContentUseCase.Response> findById(Long coverLetterId) {
        return coverLetterJpaQueryRepository.findContentProjectionById(coverLetterId)
                .map(projection -> new LoadCoverLetterContentUseCase.Response(
                        projection.getId(),
                        projection.getTitle(),
                        projection.getMemberAccountId(),
                        projection.getVisibility(),
                        projection.getContentJson(),
                        projection.getContentHtml()
                ));
    }
}

package io.hirecore.hirecorememberserver.modules.coverletter.application.usecase;

import io.hirecore.hirecorememberserver.modules.coverletter.application.port.in.LoadCoverLetterTitlesUseCase;
import io.hirecore.hirecorememberserver.modules.coverletter.application.port.out.LoadCoverLetterTitlePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LoadCoverLetterTitlesUseCaseImpl implements LoadCoverLetterTitlesUseCase {

    private final LoadCoverLetterTitlePort loadCoverLetterTitlePort;

    @Override
    @Transactional(readOnly = true)
    public Map<Long, String> execute(Collection<Long> coverLetterIds) {
        return loadCoverLetterTitlePort.findTitleMapByIds(coverLetterIds);
    }
}

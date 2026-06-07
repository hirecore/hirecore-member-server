package io.hirecore.hirecorememberserver.modules.resume.application.usecase;

import io.hirecore.hirecorememberserver.modules.resume.application.port.in.LoadResumeTitlesUseCase;
import io.hirecore.hirecorememberserver.modules.resume.application.port.out.LoadResumeTitlesPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LoadResumeTitlesUseCaseImpl implements LoadResumeTitlesUseCase {

    private final LoadResumeTitlesPort loadResumeTitlesPort;

    @Override
    @Transactional(readOnly = true)
    public Map<Long, String> execute(Collection<Long> resumeIds) {
        return loadResumeTitlesPort.findAllTitlesByIds(resumeIds);
    }
}

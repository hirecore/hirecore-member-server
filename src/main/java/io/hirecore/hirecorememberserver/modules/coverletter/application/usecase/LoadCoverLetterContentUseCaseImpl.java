package io.hirecore.hirecorememberserver.modules.coverletter.application.usecase;

import io.hirecore.hirecorememberserver.modules.coverletter.application.port.in.LoadCoverLetterContentUseCase;
import io.hirecore.hirecorememberserver.modules.coverletter.application.port.in.dto.response.CoverLetterContentLoadResult;
import io.hirecore.hirecorememberserver.modules.coverletter.application.port.out.LoadCoverLetterContentPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoadCoverLetterContentUseCaseImpl implements LoadCoverLetterContentUseCase {

    private final LoadCoverLetterContentPort loadCoverLetterContentPort;

    @Override
    @Transactional(readOnly = true)
    public Optional<CoverLetterContentLoadResult> execute(Long coverLetterId) {
        return loadCoverLetterContentPort.findById(coverLetterId);
    }
}

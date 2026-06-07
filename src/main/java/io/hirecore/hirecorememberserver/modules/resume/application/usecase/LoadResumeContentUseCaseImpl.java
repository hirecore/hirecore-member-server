package io.hirecore.hirecorememberserver.modules.resume.application.usecase;

import io.hirecore.hirecorememberserver.modules.resume.application.port.in.LoadResumeContentUseCase;
import io.hirecore.hirecorememberserver.modules.resume.application.port.out.LoadResumeContentPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoadResumeContentUseCaseImpl implements LoadResumeContentUseCase {

    private final LoadResumeContentPort loadResumeContentPort;

    @Override
    @Transactional(readOnly = true)
    public Optional<Response> execute(Long resumeId) {
        return loadResumeContentPort.findById(resumeId);
    }
}

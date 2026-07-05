package io.hirecore.hirecorememberserver.modules.coverletter.adapter.in.shared;

import io.hirecore.hirecorememberserver.modules.coverletter.application.port.in.LoadCoverLetterContentUseCase;
import io.hirecore.hirecorememberserver.modules.coverletter.application.port.in.LoadCoverLetterTitlesUseCase;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadCoverLetterContentSharedPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadCoverLetterTitleSharedPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

// 타 BC 의 자기소개서 조회용 sharedkernel out port 단일 구현
@Component
@RequiredArgsConstructor
public class CoverLetterSharedQueryAdapter implements
        LoadCoverLetterContentSharedPort,
        LoadCoverLetterTitleSharedPort
{

    private final LoadCoverLetterContentUseCase loadCoverLetterContentUseCase;
    private final LoadCoverLetterTitlesUseCase loadCoverLetterTitlesUseCase;

    @Override
    public Optional<LoadCoverLetterContentSharedPort.Result> findById(Long coverLetterId) {
        return loadCoverLetterContentUseCase.execute(coverLetterId)
                .map(result -> new LoadCoverLetterContentSharedPort.Result(
                        result.id(),
                        result.title(),
                        result.memberAccountId(),
                        result.visibility(),
                        result.contentJson(),
                        result.contentHtml()
                ));
    }

    @Override
    public Map<Long, String> findTitleMapByIds(Collection<Long> coverLetterIds) {
        return loadCoverLetterTitlesUseCase.execute(coverLetterIds);
    }
}

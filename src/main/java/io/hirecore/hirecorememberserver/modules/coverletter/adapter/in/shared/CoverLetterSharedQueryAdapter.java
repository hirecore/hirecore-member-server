package io.hirecore.hirecorememberserver.modules.coverletter.adapter.in.shared;

import io.hirecore.hirecorememberserver.modules.coverletter.application.port.in.LoadCoverLetterContentUseCase;
import io.hirecore.hirecorememberserver.modules.coverletter.application.port.in.LoadCoverLetterTitlesUseCase;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadCoverLetterContentPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadCoverLetterTitlePort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.dto.response.CoverLetterContentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * 다른 BC 가 자기소개서를 조회하기 위해 사용하는 sharedkernel out port 들의 단일 구현.
 *
 * <p>BC 당 단일 어댑터 정책에 따라 sharedkernel 의 coverletter 관련 포트들을 한 곳에서 구현한다.
 * 각 메서드는 BC 내부 UseCase 를 경유해 트랜잭션 경계와 헥사고날 in/out 경계를 보존한다.</p>
 */
@Component
@RequiredArgsConstructor
public class CoverLetterSharedQueryAdapter implements
        LoadCoverLetterContentPort,
        LoadCoverLetterTitlePort
{

    private final LoadCoverLetterContentUseCase loadCoverLetterContentUseCase;
    private final LoadCoverLetterTitlesUseCase loadCoverLetterTitlesUseCase;

    @Override
    public Optional<CoverLetterContentResult> findById(Long coverLetterId) {
        return loadCoverLetterContentUseCase.execute(coverLetterId)
                .map(result -> new CoverLetterContentResult(
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

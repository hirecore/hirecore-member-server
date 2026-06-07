package io.hirecore.hirecorememberserver.modules.coverletter.adapter.in.shared;

import io.hirecore.hirecorememberserver.modules.coverletter.application.port.in.LoadCoverLetterTitlesUseCase;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadCoverLetterTitlesPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

/**
 * 다른 BC 가 자기소개서 제목을 조회하기 위해 사용하는 sharedkernel out port 의 구현.
 *
 * <p>BC 내부 UseCase 를 경유해 트랜잭션 경계와 헥사고날 in/out 경계를 모두 보존한다.</p>
 */
@Component
@RequiredArgsConstructor
public class CoverLetterTitleQueryAdapter implements LoadCoverLetterTitlesPort {

    private final LoadCoverLetterTitlesUseCase loadCoverLetterTitlesUseCase;

    @Override
    public Map<Long, String> findAllTitlesByIds(Collection<Long> coverLetterIds) {
        return loadCoverLetterTitlesUseCase.execute(coverLetterIds);
    }
}

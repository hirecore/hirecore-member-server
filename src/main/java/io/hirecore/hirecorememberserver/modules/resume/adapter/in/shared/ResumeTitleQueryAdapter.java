package io.hirecore.hirecorememberserver.modules.resume.adapter.in.shared;

import io.hirecore.hirecorememberserver.modules.resume.application.port.in.LoadResumeTitlesUseCase;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadResumeTitlesPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

/**
 * 다른 BC 가 이력서 제목을 조회하기 위해 사용하는 sharedkernel out port 의 구현.
 *
 * <p>BC 내부 UseCase 를 경유해 트랜잭션 경계와 헥사고날 in/out 경계를 모두 보존한다.</p>
 */
@Component
@RequiredArgsConstructor
public class ResumeTitleQueryAdapter implements LoadResumeTitlesPort {

    private final LoadResumeTitlesUseCase loadResumeTitlesUseCase;

    @Override
    public Map<Long, String> findAllTitlesByIds(Collection<Long> resumeIds) {
        return loadResumeTitlesUseCase.execute(resumeIds);
    }
}

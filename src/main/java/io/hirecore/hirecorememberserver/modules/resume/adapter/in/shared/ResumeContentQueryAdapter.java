package io.hirecore.hirecorememberserver.modules.resume.adapter.in.shared;

import io.hirecore.hirecorememberserver.modules.resume.application.port.in.LoadResumeContentUseCase;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadResumeContentPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.dto.response.ResumeContentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 다른 BC 가 이력서의 본문/메타데이터를 조회하기 위해 사용하는 sharedkernel out port 의 구현.
 *
 * <p>BC 내부 UseCase 를 경유해 트랜잭션 경계와 헥사고날 in/out 경계를 모두 보존한다.
 * BC 내부 응답 DTO 를 sharedkernel DTO 로 매핑해 외부에 노출한다.</p>
 */
@Component
@RequiredArgsConstructor
public class ResumeContentQueryAdapter implements LoadResumeContentPort {

    private final LoadResumeContentUseCase loadResumeContentUseCase;

    @Override
    public Optional<ResumeContentResult> findById(Long resumeId) {
        return loadResumeContentUseCase.execute(resumeId)
                .map(result -> new ResumeContentResult(
                        result.id(),
                        result.title(),
                        result.memberAccountId(),
                        result.visibility(),
                        result.contentJson(),
                        result.contentHtml()
                ));
    }
}

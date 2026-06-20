package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationException;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.DeletePortfolioUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.DeletePortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.PublishDomainEventsPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *  [로직 플로우]
 *   1. Portfolio aggregate 로드 → 없으면 PORTFOLIO_NOT_FOUND
 *   2. portfolio.delete(viewerId) 호출 → 본인 소유 검증(도메인) + 참조 이미지의 PortfolioImagesUnlinkedEvent 발행
 *   3. DeletePortfolioPort.delete(portfolio) → portfolio_member_interests / portfolio_member_views cleanup 후
 *      portfolios + cascade 자식 (contents/job_categories/tags) 영구 삭제
 *   4. publishDomainEventsPort.publishAll(portfolio) → 도메인 이벤트 발행 위임
 *
 *  [부수효과]
 *   - 빠진 이미지: portfolio.delete 가 발행한 PortfolioImagesUnlinkedEvent 를 file BC 가 BEFORE_COMMIT 으로 받아
 *     ORPHANED 로 전이시키고, 그 결과 발행되는 ImageOrphanedEvent 를 storage BC 가 AFTER_COMMIT 으로 받아 사용량을 차감합니다.
 */
@Service
@RequiredArgsConstructor
public class DeletePortfolioUseCaseImpl implements DeletePortfolioUseCase {

    private final LoadPortfolioPort loadPortfolioPort;
    private final DeletePortfolioPort deletePortfolioPort;
    private final PublishDomainEventsPort publishDomainEventsPort;

    @Override
    @Transactional
    public void execute(Long portfolioId, Long viewerId) {
        Portfolio portfolio = loadPortfolioPort.findById(portfolioId)
                .orElseThrow(() -> new PortfolioApplicationException(
                        PortfolioApplicationExceptionCodeCluster.DetailResponse.PORTFOLIO_NOT_FOUND
                ));
        portfolio.delete(viewerId);
        deletePortfolioPort.delete(portfolio);
        publishDomainEventsPort.publishAll(portfolio);
    }
}

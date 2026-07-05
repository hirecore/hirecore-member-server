package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationException;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.DeletePortfolioUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.DeletePortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.PublishDomainEventsSharedPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 포트폴리오 삭제: 소유 검증 후 영구 삭제, 참조 이미지는 이벤트로 회수 (save 없는 경로라 publishAll 명시 호출)
@Service
@RequiredArgsConstructor
public class DeletePortfolioUseCaseImpl implements DeletePortfolioUseCase {

    private final LoadPortfolioPort loadPortfolioPort;
    private final DeletePortfolioPort deletePortfolioPort;
    private final PublishDomainEventsSharedPort publishDomainEventsSharedPort;

    @Override
    @Transactional
    public void execute(Long portfolioId, Long viewerId) {
        Portfolio portfolio = loadPortfolioPort.findById(portfolioId)
                .orElseThrow(() -> new PortfolioApplicationException(
                        PortfolioApplicationExceptionCodeCluster.DetailResponse.PORTFOLIO_NOT_FOUND
                ));
        portfolio.delete(viewerId);
        deletePortfolioPort.delete(portfolio);
        publishDomainEventsSharedPort.publishAll(portfolio);
    }
}

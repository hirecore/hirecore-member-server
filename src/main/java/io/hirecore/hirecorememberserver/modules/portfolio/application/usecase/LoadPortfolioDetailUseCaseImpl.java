package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import io.hirecore.hirecorememberserver.modules.portfolio.application.assembler.PortfolioDetailAssembler;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationException;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadPortfolioDetailUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.ExistsPortfolioMemberInterestPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.ExistsPortfolioMemberViewPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadProfileNicknameSharedPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.PublishDomainEventsSharedPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoadPortfolioDetailUseCaseImpl implements LoadPortfolioDetailUseCase {

    private final LoadPortfolioPort loadPortfolioPort;
    private final LoadProfileNicknameSharedPort loadProfileNicknamePort;
    private final ExistsPortfolioMemberViewPort existsPortfolioMemberViewPort;
    private final ExistsPortfolioMemberInterestPort existsPortfolioMemberInterestPort;
    private final PublishDomainEventsSharedPort publishDomainEventsPort;
    private final PortfolioDetailAssembler portfolioDetailAssembler;

    @Override
    @Transactional(readOnly = true)
    public Response execute(Long portfolioId, Long viewerId) {
        Portfolio portfolio = loadPortfolio(portfolioId);
        boolean isOwner = portfolio.isOwnedBy(viewerId);
        ensureAccessible(portfolio, viewerId);

        String publisherNickname = loadProfileNicknamePort.findNickname(portfolio.getMemberAccountId())
                .orElseThrow(() -> new PortfolioApplicationException(
                        PortfolioApplicationExceptionCodeCluster.DetailResponse.PORTFOLIO_NICKNAME_NOT_FOUND
                ));

        long displayedViewCount = resolveDisplayedViewCount(portfolio, viewerId);
        Boolean isInterested = resolveIsInterested(portfolio, viewerId, isOwner);

        return Response.builder()
                .isOwner(isOwner)
                .viewCount(displayedViewCount)
                .interestCount(portfolio.getCachedInterestCount())
                .isInterested(isInterested)
                .updatedAt(portfolio.latestUpdatedAt())
                .portfolio(portfolioDetailAssembler.buildBody(portfolio))
                .publisher(portfolioDetailAssembler.buildPublisher(portfolio, publisherNickname))
                .linkedResume(portfolioDetailAssembler.buildLinkedResume(portfolio.getResumeId(), viewerId))
                .linkedCoverLetter(portfolioDetailAssembler.buildLinkedCoverLetter(portfolio.getCoverLetterId(), viewerId))
                .build();
    }

    private Boolean resolveIsInterested(Portfolio portfolio, Long viewerId, boolean isOwner) {
        if (viewerId == null || isOwner) {
            return null;
        }
        return existsPortfolioMemberInterestPort.exists(portfolio.getId(), viewerId);
    }

    private long resolveDisplayedViewCount(Portfolio portfolio, Long viewerId) {
        long cachedViewCount = portfolio.getCachedViewCount();
        if (viewerId == null) {
            return cachedViewCount;
        }
        boolean alreadyViewed = existsPortfolioMemberViewPort.exists(portfolio.getId(), viewerId);
        boolean recorded = portfolio.markViewedBy(viewerId, alreadyViewed);
        if (!recorded) {
            return cachedViewCount;
        }
        publishDomainEventsPort.publishAll(portfolio);
        return cachedViewCount + 1;
    }

    private Portfolio loadPortfolio(Long portfolioId) {
        return loadPortfolioPort.findById(portfolioId)
                .orElseThrow(() -> new PortfolioApplicationException(
                        PortfolioApplicationExceptionCodeCluster.DetailResponse.PORTFOLIO_NOT_FOUND
                ));
    }

    private void ensureAccessible(Portfolio portfolio, Long viewerId) {
        if (!portfolio.isViewableBy(viewerId)) {
            throw new PortfolioApplicationException(
                    PortfolioApplicationExceptionCodeCluster.DetailResponse.PORTFOLIO_FORBIDDEN
            );
        }
    }
}

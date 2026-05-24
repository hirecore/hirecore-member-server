package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationException;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadPortfolioDetailUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response.PortfolioContentResponse;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response.PortfolioDetailResponse;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response.PortfolioTagResponse;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPortfolioTagPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadProfileNicknamePort;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoadPortfolioDetailUseCaseImpl implements LoadPortfolioDetailUseCase {

    private final LoadPortfolioPort loadPortfolioPort;
    private final LoadProfileNicknamePort loadProfileNicknamePort;
    private final LoadPortfolioTagPort loadPortfolioTagPort;

    @Override
    @Transactional(readOnly = true)
    public PortfolioDetailResponse execute(Long portfolioId, Long viewerId) {
        Portfolio portfolio = loadPortfolio(portfolioId);
        boolean isOwner = portfolio.getMemberAccountId().equals(viewerId);
        ensureAccessible(portfolio, isOwner);

        String publisherNickname = loadProfileNicknamePort.findNickname(portfolio.getMemberAccountId())
                .orElseThrow(() -> new PortfolioApplicationException(
                        PortfolioApplicationExceptionCodeCluster.DetailResponse.PORTFOLIO_NICKNAME_NOT_FOUND
                ));

        List<PortfolioTagResponse> tags = loadPortfolioTagPort.findTags(portfolioId);

        return buildResponse(portfolio, publisherNickname, isOwner, tags);
    }

    private Portfolio loadPortfolio(Long portfolioId) {
        return loadPortfolioPort.findPortfolio(portfolioId)
                .orElseThrow(() -> new PortfolioApplicationException(
                        PortfolioApplicationExceptionCodeCluster.DetailResponse.PORTFOLIO_NOT_FOUND
                ));
    }

    private void ensureAccessible(Portfolio portfolio, boolean isOwner) {
        if (portfolio.getVisibility() != Visibility.PUBLIC && !isOwner) {
            throw new PortfolioApplicationException(
                    PortfolioApplicationExceptionCodeCluster.DetailResponse.PORTFOLIO_FORBIDDEN
            );
        }
    }

    private PortfolioDetailResponse buildResponse(
            Portfolio portfolio,
            String publisherNickname,
            boolean isOwner,
            List<PortfolioTagResponse> tags
    ) {
        PortfolioContentResponse content = PortfolioContentResponse.builder()
                .json(portfolio.getPortfolioContent().getContentJson())
                .html(portfolio.getPortfolioContent().getContentHtml())
                .build();

        return PortfolioDetailResponse.builder()
                .isOwner(isOwner)
                .publisher(publisherNickname)
                .collaborationType(portfolio.getCollaborationType())
                .visibility(portfolio.getVisibility())
                .title(portfolio.getTitle())
                .content(content)
                .tags(tags)
                .externalLinks(portfolio.getExternalLinks())
                .build();
    }
}

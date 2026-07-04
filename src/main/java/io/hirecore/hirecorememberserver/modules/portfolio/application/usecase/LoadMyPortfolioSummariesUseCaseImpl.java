package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import io.hirecore.hirecorememberserver.modules.portfolio.application.assembler.MyPortfolioSummariesAssembler;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadMyPortfolioSummariesUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoadMyPortfolioSummariesUseCaseImpl implements LoadMyPortfolioSummariesUseCase {

    private final LoadPortfolioPort loadPortfolioPort;
    private final MyPortfolioSummariesAssembler myPortfolioSummariesAssembler;

    // 본인 포트폴리오 목록 조회 후 응답 조립은 어셈블러에 위임
    @Override
    @Transactional(readOnly = true)
    public Response execute(Long viewerId) {
        List<Portfolio> portfolios = loadPortfolioPort
                .findAllByMemberAccountIdOrderByUpdatedAtDesc(viewerId);
        if (portfolios.isEmpty()) {
            return new Response(List.of());
        }
        return new Response(myPortfolioSummariesAssembler.buildItems(portfolios));
    }
}

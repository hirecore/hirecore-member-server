package io.hirecore.hirecorememberserver.modules.portfolio.application.port.out;

import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;

import java.util.List;

public interface LoadPortfoliosByMemberPort {

    /**
     * 한 회원이 작성한 모든 포트폴리오를 마지막 수정 시각 내림차순으로 반환한다.
     */
    List<Portfolio> findAllByMemberAccountIdOrderByUpdatedAtDesc(Long memberAccountId);

    /**
     * 한 회원이 작성한 PUBLIC 포트폴리오 중 지정된 식별자를 제외한 전체를
     * 마지막 수정 시각 내림차순으로 반환한다.
     *
     * <p>포트폴리오 상세 페이지의 "작성자의 다른 작품" 영역 합성에 사용된다.
     * 다른 사람에게 노출되는 영역이므로 PRIVATE 작품은 항상 제외하며,
     * 본 포트폴리오 자체는 {@code excludedPortfolioId} 로 제외한다.</p>
     */
    List<Portfolio> findAllPublicByMemberAccountIdExcludingOrderByUpdatedAtDesc(
            Long memberAccountId,
            Long excludedPortfolioId
    );
}

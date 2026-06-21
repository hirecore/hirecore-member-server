package io.hirecore.hirecorememberserver.modules.portfolio.application.port.out;

import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;

import java.time.Instant;
import java.util.List;

public interface LoadPublicPortfolioSummaryPort {

    /**
     * 공개({@code visibility = PUBLIC}) 포트폴리오를
     * effective updatedAt (Portfolio / PortfolioContent / PortfolioJobCategory.connectedAt / PortfolioTag 의 GREATEST) 내림차순,
     * 동률 시 portfolioId 내림차순으로 가져온다.
     *
     * <p>{@code cursorEffectiveUpdatedAt} 와 {@code cursorPortfolioId} 가 모두 {@code null} 이면 첫 페이지,
     * 모두 non-null 이면 그 위치 이후 페이지를 반환한다. 두 값은 묶음으로 다뤄지므로 항상 함께 전달돼야 한다.</p>
     *
     * <p>{@code limit} 은 호출 측이 hasNext 판정을 위해 size + 1 같이 한 개 더 요청할 수 있도록 그대로 노출된다.</p>
     */
    List<PublicPortfolioRow> findPublicPortfoliosOrderByEffectiveUpdatedAtDesc(
            Instant cursorEffectiveUpdatedAt,
            Long cursorPortfolioId,
            int limit
    );

    /**
     * @param portfolio          서브 집계({@code portfolioContent}, {@code portfolioJobCategory}, {@code portfolioTags})
     *                           가 모두 로드된 상태의 도메인 객체.
     * @param effectiveUpdatedAt 위 SQL 정렬 기준으로 사용된 GREATEST 결과값.
     *                           응답 노출용 updatedAt 으로도 사용된다.
     */
    record PublicPortfolioRow(
            Portfolio portfolio,
            Instant effectiveUpdatedAt
    ) {}
}

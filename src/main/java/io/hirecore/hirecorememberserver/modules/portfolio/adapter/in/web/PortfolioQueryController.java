package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.response.PortfolioDetailApiResponse;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.response.PortfolioEditApiResponse;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.mapper.PortfolioWebMapper;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadPortfolioDetailUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadPortfolioEditUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response.PortfolioDetailResponse;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response.PortfolioEditResponse;
import io.hirecore.hirecorememberserver.sharedkernel.application.security.AuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portfolios")
public class PortfolioQueryController {

    private final LoadPortfolioDetailUseCase loadPortfolioDetailUseCase;
    private final LoadPortfolioEditUseCase loadPortfolioEditUseCase;
    private final PortfolioWebMapper portfolioWebMapper;

    /**
     * 비로그인 사용자도 접근 가능한 상세 조회입니다. 작성자 본인 호출 시 viewCount/interestCount 등 통계는 변하지 않습니다.
     * {@code authPrincipal} 은 비로그인 호출에서 {@code null} 일 수 있습니다.
     */
    @GetMapping("{portfolioId}")
    public ResponseEntity<PortfolioDetailApiResponse> loadPortfolioDetail(
            @AuthenticationPrincipal AuthPrincipal authPrincipal,
            @PathVariable("portfolioId") Long portfolioId
    ){
        Long viewerId = authPrincipal != null ? authPrincipal.id() : null;
        PortfolioDetailResponse applicationResponse = loadPortfolioDetailUseCase.execute(portfolioId, viewerId);
        PortfolioDetailApiResponse response = portfolioWebMapper.toPortfolioDetailApiResponse(applicationResponse);
        return ResponseEntity.ok(response);
    }

    /**
     * 작성자 본인만 접근 가능한 편집 폼 초기화 데이터입니다.
     * SecurityConfig 에서 {@code authenticated} 로 보호되므로 {@code authPrincipal} 은 항상 non-null 입니다.
     */
    @GetMapping("{portfolioId}/edit")
    public ResponseEntity<PortfolioEditApiResponse> editPortfolio(
            @AuthenticationPrincipal AuthPrincipal authPrincipal,
            @PathVariable("portfolioId") Long portfolioId
    ){
        PortfolioEditResponse applicationResponse = loadPortfolioEditUseCase.execute(portfolioId, authPrincipal.id());
        PortfolioEditApiResponse response = portfolioWebMapper.toPortfolioEditApiResponse(applicationResponse);
        return ResponseEntity.ok(response);
    }
}

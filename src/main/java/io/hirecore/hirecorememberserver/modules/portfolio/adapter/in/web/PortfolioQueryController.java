package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.response.MyPortfolioSummariesApiResponse;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.response.PortfolioDetailApiResponse;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.response.PortfolioEditApiResponse;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.mapper.PortfolioWebMapper;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadMyPortfolioSummariesUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadPortfolioDetailUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadPortfolioEditUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response.MyPortfolioSummariesResponse;
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
    private final LoadMyPortfolioSummariesUseCase loadMyPortfolioSummariesUseCase;
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
    public ResponseEntity<PortfolioEditApiResponse> loadPortfolioEdit(
            @AuthenticationPrincipal AuthPrincipal authPrincipal,
            @PathVariable("portfolioId") Long portfolioId
    ){
        PortfolioEditResponse applicationResponse = loadPortfolioEditUseCase.execute(portfolioId, authPrincipal.id());
        PortfolioEditApiResponse response = portfolioWebMapper.toPortfolioEditApiResponse(applicationResponse);
        return ResponseEntity.ok(response);
    }

    /**
     * 작성자 본인이 보유한 모든 포트폴리오를 마지막 수정 시각 내림차순으로 한 번에 반환합니다.
     * 페이징 없이 전체를 반환하며, 연결된 이력서/자기소개서가 없는 경우 각 필드는 {@code null} 입니다.
     * SecurityConfig 에서 {@code authenticated} 로 보호되므로 {@code authPrincipal} 은 항상 non-null 입니다.
     */
    @GetMapping("summaries/mine")
    public ResponseEntity<MyPortfolioSummariesApiResponse> loadMyPortfolioSummaries(
            @AuthenticationPrincipal AuthPrincipal authPrincipal
    ){
        MyPortfolioSummariesResponse applicationResponse = loadMyPortfolioSummariesUseCase.execute(authPrincipal.id());
        MyPortfolioSummariesApiResponse response = portfolioWebMapper.toMyPortfolioSummariesApiResponse(applicationResponse);
        return ResponseEntity.ok(response);
    }
}

package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.LoadMyPortfolioSummariesApi;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.LoadPortfolioDetailApi;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.LoadPortfolioEditApi;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.LoadPublicPortfolioSummariesApi;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.mapper.PortfolioWebMapper;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadMyPortfolioSummariesUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadPortfolioDetailUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadPortfolioEditUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadPublicPortfolioSummariesUseCase;
import io.hirecore.hirecorememberserver.sharedkernel.application.security.AuthPrincipal;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portfolios")
@Validated
public class PortfolioQueryController {

    private final LoadPortfolioDetailUseCase loadPortfolioDetailUseCase;
    private final LoadPortfolioEditUseCase loadPortfolioEditUseCase;
    private final LoadMyPortfolioSummariesUseCase loadMyPortfolioSummariesUseCase;
    private final LoadPublicPortfolioSummariesUseCase loadPublicPortfolioSummariesUseCase;
    private final PortfolioWebMapper portfolioWebMapper;

    /**
     * 비로그인 사용자도 접근 가능한 상세 조회입니다. 작성자 본인 호출 시 viewCount/interestCount 등 통계는 변하지 않습니다.
     * {@code authPrincipal} 은 비로그인 호출에서 {@code null} 일 수 있습니다.
     */
    @GetMapping("{portfolioId}")
    public ResponseEntity<LoadPortfolioDetailApi.Response> loadPortfolioDetail(
            @AuthenticationPrincipal AuthPrincipal authPrincipal,
            @PathVariable("portfolioId") Long portfolioId
    ){
        Long viewerId = authPrincipal != null ? authPrincipal.id() : null;
        LoadPortfolioDetailUseCase.Response applicationResponse = loadPortfolioDetailUseCase.execute(portfolioId, viewerId);
        LoadPortfolioDetailApi.Response response = portfolioWebMapper.toLoadPortfolioDetailApiResponse(applicationResponse);
        return ResponseEntity.ok(response);
    }

    /**
     * 작성자 본인만 접근 가능한 편집 폼 초기화 데이터입니다.
     * SecurityConfig 에서 {@code authenticated} 로 보호되므로 {@code authPrincipal} 은 항상 non-null 입니다.
     */
    @GetMapping("{portfolioId}/edit")
    public ResponseEntity<LoadPortfolioEditApi.Response> loadPortfolioEdit(
            @AuthenticationPrincipal AuthPrincipal authPrincipal,
            @PathVariable("portfolioId") Long portfolioId
    ){
        LoadPortfolioEditUseCase.Response applicationResponse = loadPortfolioEditUseCase.execute(portfolioId, authPrincipal.id());
        LoadPortfolioEditApi.Response response = portfolioWebMapper.toLoadPortfolioEditApiResponse(applicationResponse);
        return ResponseEntity.ok(response);
    }

    /**
     * 작성자 본인이 보유한 모든 포트폴리오를 마지막 수정 시각 내림차순으로 한 번에 반환합니다.
     * 페이징 없이 전체를 반환하며, 연결된 이력서/자기소개서가 없는 경우 각 필드는 {@code null} 입니다.
     * SecurityConfig 에서 {@code authenticated} 로 보호되므로 {@code authPrincipal} 은 항상 non-null 입니다.
     */
    @GetMapping("summaries/mine")
    public ResponseEntity<LoadMyPortfolioSummariesApi.Response> loadMyPortfolioSummaries(
            @AuthenticationPrincipal AuthPrincipal authPrincipal
    ){
        LoadMyPortfolioSummariesUseCase.Response applicationResponse = loadMyPortfolioSummariesUseCase.execute(authPrincipal.id());
        LoadMyPortfolioSummariesApi.Response response = portfolioWebMapper.toLoadMyPortfolioSummariesApiResponse(applicationResponse);
        return ResponseEntity.ok(response);
    }

    /**
     * 비로그인 사용자도 접근 가능한 공개 포트폴리오 요약 목록(무한 스크롤)입니다.
     * effective updatedAt (Portfolio/PortfolioContent/PortfolioJobCategory.connectedAt/PortfolioTag 의 GREATEST) 내림차순으로 정렬됩니다.
     * {@code authPrincipal} 은 비로그인 호출에서 {@code null} 일 수 있으며, 각 항목의 {@code isOwner} 판정에만 사용됩니다.
     */
    @GetMapping("summaries/public")
    public ResponseEntity<LoadPublicPortfolioSummariesApi.Response> loadPublicPortfolioSummaries(
            @AuthenticationPrincipal AuthPrincipal authPrincipal,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") @Min(1) @Max(50) int size
    ){
        Long viewerId = authPrincipal != null ? authPrincipal.id() : null;
        LoadPublicPortfolioSummariesUseCase.Response applicationResponse =
                loadPublicPortfolioSummariesUseCase.execute(cursor, size, viewerId);
        LoadPublicPortfolioSummariesApi.Response response =
                portfolioWebMapper.toLoadPublicPortfolioSummariesApiResponse(applicationResponse);
        return ResponseEntity.ok(response);
    }
}

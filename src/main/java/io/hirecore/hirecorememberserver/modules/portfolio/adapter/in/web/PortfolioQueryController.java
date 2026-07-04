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

    // 상세 조회 (비로그인 허용, authPrincipal null 가능)
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

    // 편집 폼 초기화 데이터 (작성자 본인 전용)
    @GetMapping("{portfolioId}/edit")
    public ResponseEntity<LoadPortfolioEditApi.Response> loadPortfolioEdit(
            @AuthenticationPrincipal AuthPrincipal authPrincipal,
            @PathVariable("portfolioId") Long portfolioId
    ){
        LoadPortfolioEditUseCase.Response applicationResponse = loadPortfolioEditUseCase.execute(portfolioId, authPrincipal.id());
        LoadPortfolioEditApi.Response response = portfolioWebMapper.toLoadPortfolioEditApiResponse(applicationResponse);
        return ResponseEntity.ok(response);
    }

    // 본인 보유 포트폴리오 전체 목록 (수정 시각 내림차순, 페이징 없음)
    @GetMapping("summaries/mine")
    public ResponseEntity<LoadMyPortfolioSummariesApi.Response> loadMyPortfolioSummaries(
            @AuthenticationPrincipal AuthPrincipal authPrincipal
    ){
        LoadMyPortfolioSummariesUseCase.Response applicationResponse = loadMyPortfolioSummariesUseCase.execute(authPrincipal.id());
        LoadMyPortfolioSummariesApi.Response response = portfolioWebMapper.toLoadMyPortfolioSummariesApiResponse(applicationResponse);
        return ResponseEntity.ok(response);
    }

    // 공개 포트폴리오 요약 목록 무한 스크롤 (비로그인 허용, authPrincipal은 isOwner 판정용)
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

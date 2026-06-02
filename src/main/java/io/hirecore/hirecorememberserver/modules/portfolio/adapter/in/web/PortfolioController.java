package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.request.CreatePortfolioApiRequest;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.request.UpdatePortfolioApiRequest;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.response.CreatePortfolioApiResponse;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.response.PortfolioDetailApiResponse;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.response.PortfolioEditApiResponse;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.response.UpdatePortfolioApiResponse;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.mapper.PortfolioWebMapper;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.CreatePortfolioUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadPortfolioDetailUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadPortfolioEditUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.UpdatePortfolioUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request.CreatePortfolioCommand;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request.UpdatePortfolioCommand;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response.PortfolioDetailResponse;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response.PortfolioEditResponse;
import io.hirecore.hirecorememberserver.sharedkernel.application.security.AuthPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portfolios")
public class PortfolioController {

    private final CreatePortfolioUseCase createPortfolioUseCase;
    private final LoadPortfolioDetailUseCase loadPortfolioDetailUseCase;
    private final LoadPortfolioEditUseCase loadPortfolioEditUseCase;
    private final UpdatePortfolioUseCase updatePortfolioUseCase;
    private final PortfolioWebMapper portfolioWebMapper;

    @PostMapping
    public ResponseEntity<CreatePortfolioApiResponse> createPortfolio(
            @AuthenticationPrincipal AuthPrincipal authPrincipal,
            @Valid @RequestBody CreatePortfolioApiRequest request
    ){
        CreatePortfolioCommand command = portfolioWebMapper.toCreatePortfolioCommand(request);
        Long portfolioId = createPortfolioUseCase.execute(authPrincipal.id(), command);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(portfolioId)
                .toUri();

        return ResponseEntity.created(location).body(new CreatePortfolioApiResponse(portfolioId));
    }

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

    @GetMapping("{portfolioId}/edit")
    public ResponseEntity<PortfolioEditApiResponse> editPortfolio(
            @AuthenticationPrincipal AuthPrincipal authPrincipal,
            @PathVariable("portfolioId") Long portfolioId
    ){
        Long viewerId = authPrincipal != null ? authPrincipal.id() : null;
        PortfolioEditResponse applicationResponse = loadPortfolioEditUseCase.execute(portfolioId, viewerId);
        PortfolioEditApiResponse response = portfolioWebMapper.toPortfolioEditApiResponse(applicationResponse);
        return ResponseEntity.ok(response);
    }

    @PutMapping("{portfolioId}")
    public ResponseEntity<UpdatePortfolioApiResponse> updatePortfolio(
            @AuthenticationPrincipal AuthPrincipal authPrincipal,
            @PathVariable("portfolioId") Long portfolioId,
            @Valid @RequestBody UpdatePortfolioApiRequest request
    ){
        Long viewerId = authPrincipal != null ? authPrincipal.id() : null;
        UpdatePortfolioCommand command = portfolioWebMapper.toUpdatePortfolioCommand(request);
        Long updatedPortfolioId = updatePortfolioUseCase.execute(portfolioId, viewerId, command);
        return ResponseEntity.ok(new UpdatePortfolioApiResponse(updatedPortfolioId));
    }
}

package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.CreatePortfolioApi;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.UpdatePortfolioApi;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.mapper.PortfolioWebMapper;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.CancelPortfolioInterestUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.CreatePortfolioUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.DeletePortfolioUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.RegisterPortfolioInterestUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.UpdatePortfolioUseCase;
import io.hirecore.hirecorememberserver.sharedkernel.application.security.AuthPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portfolios")
public class PortfolioCommandController {

    private final CreatePortfolioUseCase createPortfolioUseCase;
    private final UpdatePortfolioUseCase updatePortfolioUseCase;
    private final DeletePortfolioUseCase deletePortfolioUseCase;
    private final RegisterPortfolioInterestUseCase registerPortfolioInterestUseCase;
    private final CancelPortfolioInterestUseCase cancelPortfolioInterestUseCase;
    private final PortfolioWebMapper portfolioWebMapper;

    @PostMapping
    public ResponseEntity<CreatePortfolioApi.Response> createPortfolio(
            @AuthenticationPrincipal AuthPrincipal authPrincipal,
            @Valid @RequestBody CreatePortfolioApi.Request request
    ){
        CreatePortfolioUseCase.Command command = portfolioWebMapper.toCreatePortfolioCommand(request);
        Long portfolioId = createPortfolioUseCase.execute(authPrincipal.id(), command);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(portfolioId)
                .toUri();

        return ResponseEntity.created(location).body(new CreatePortfolioApi.Response(portfolioId));
    }

    @PutMapping("{portfolioId}")
    public ResponseEntity<UpdatePortfolioApi.Response> updatePortfolio(
            @AuthenticationPrincipal AuthPrincipal authPrincipal,
            @PathVariable("portfolioId") Long portfolioId,
            @Valid @RequestBody UpdatePortfolioApi.Request request
    ){
        UpdatePortfolioUseCase.Command command = portfolioWebMapper.toUpdatePortfolioCommand(request);
        Long updatedPortfolioId = updatePortfolioUseCase.execute(portfolioId, authPrincipal.id(), command);
        return ResponseEntity.ok(new UpdatePortfolioApi.Response(updatedPortfolioId));
    }

    @DeleteMapping("{portfolioId}")
    public ResponseEntity<Void> deletePortfolio(
            @AuthenticationPrincipal AuthPrincipal authPrincipal,
            @PathVariable("portfolioId") Long portfolioId
    ){
        deletePortfolioUseCase.execute(portfolioId, authPrincipal.id());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("{portfolioId}/interest")
    public ResponseEntity<Void> registerPortfolioInterest(
            @AuthenticationPrincipal AuthPrincipal authPrincipal,
            @PathVariable("portfolioId") Long portfolioId
    ){
        registerPortfolioInterestUseCase.execute(portfolioId, authPrincipal.id());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{portfolioId}/interest")
    public ResponseEntity<Void> cancelPortfolioInterest(
            @AuthenticationPrincipal AuthPrincipal authPrincipal,
            @PathVariable("portfolioId") Long portfolioId
    ){
        cancelPortfolioInterestUseCase.execute(portfolioId, authPrincipal.id());
        return ResponseEntity.noContent().build();
    }
}

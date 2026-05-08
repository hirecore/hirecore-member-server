package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.request.CreatePortfolioApiRequest;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.mapper.PortfolioWebMapper;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.CreatePortfolioUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request.CreatePortfolioCommand;
import io.hirecore.hirecorememberserver.sharedkernel.application.security.AuthPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portfolios")
public class PortfolioController {

    private final CreatePortfolioUseCase createPortfolioUseCase;
    private final PortfolioWebMapper portfolioWebMapper;

    @PostMapping
    public ResponseEntity<Void> createPortfolio(
            @AuthenticationPrincipal AuthPrincipal authPrincipal,
            @Valid @RequestBody CreatePortfolioApiRequest request
    ){
        CreatePortfolioCommand command = portfolioWebMapper.toCommand(request);
        createPortfolioUseCase.execute(authPrincipal.id(), command);

        return ResponseEntity.ok().build();
    }
}

package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.request.CreatePortfolioApiRequest;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.response.CreatePortfolioApiResponse;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portfolios")
public class PortfolioController {

    private final CreatePortfolioUseCase createPortfolioUseCase;
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
}

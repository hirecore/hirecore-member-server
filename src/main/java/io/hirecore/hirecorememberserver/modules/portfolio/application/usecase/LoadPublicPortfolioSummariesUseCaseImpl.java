package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import io.hirecore.hirecorememberserver.modules.portfolio.application.assembler.PublicPortfolioSummariesAssembler;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadPublicPortfolioSummariesUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPublicPortfolioSummaryPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPublicPortfolioSummaryPort.PublicPortfolioRow;
import io.hirecore.hirecorememberserver.sharedkernel.application.cursor.EffectiveTimeCursor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoadPublicPortfolioSummariesUseCaseImpl implements LoadPublicPortfolioSummariesUseCase {

    private final LoadPublicPortfolioSummaryPort loadPublicPortfolioSummaryPort;
    private final PublicPortfolioSummariesAssembler publicPortfolioSummariesAssembler;

    // 커서 페이징(size+1로 hasNext 판정) 후 응답 조립은 어셈블러에 위임
    @Override
    @Transactional(readOnly = true)
    public Response execute(String cursorToken, int size, Long viewerId) {
        EffectiveTimeCursor cursor = (cursorToken == null || cursorToken.isBlank())
                ? null
                : EffectiveTimeCursor.decode(cursorToken);

        List<PublicPortfolioRow> rows = loadPublicPortfolioSummaryPort
                .findPublicPortfoliosOrderByEffectiveUpdatedAtDesc(
                        cursor != null ? cursor.time() : null,
                        cursor != null ? cursor.id() : null,
                        size + 1
                );

        boolean hasNext = rows.size() > size;
        List<PublicPortfolioRow> pageRows = hasNext ? rows.subList(0, size) : rows;

        List<Response.Item> items = publicPortfolioSummariesAssembler.buildItems(pageRows, viewerId);

        String nextCursor = hasNext ? encodeNextCursor(pageRows) : null;
        Response.Pagination pagination = new Response.Pagination(nextCursor, hasNext);
        return new Response(items, pagination);
    }

    private static String encodeNextCursor(List<PublicPortfolioRow> pageRows) {
        PublicPortfolioRow last = pageRows.get(pageRows.size() - 1);
        return new EffectiveTimeCursor(
                last.effectiveUpdatedAt(),
                last.portfolio().getId()
        ).encode();
    }
}

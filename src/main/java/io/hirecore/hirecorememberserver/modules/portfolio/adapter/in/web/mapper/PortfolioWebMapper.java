package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.mapper;

import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.CreatePortfolioApi;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.LoadMyPortfolioSummariesApi;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.LoadPortfolioDetailApi;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.LoadPortfolioEditApi;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.LoadPublicPortfolioSummariesApi;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.UpdatePortfolioApi;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.CreatePortfolioUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadMyPortfolioSummariesUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadPortfolioDetailUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadPortfolioEditUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadPublicPortfolioSummariesUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.UpdatePortfolioUseCase;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.SharedRequestApiDto;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.SharedResponseApiDto;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.mapper.SharedDomainVoWebMapper;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.in.dto.SharedCommandDto;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.in.dto.SharedResponseDto;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapStructConfig.class,
        uses = {SharedDomainVoWebMapper.class}
)
public abstract class PortfolioWebMapper {

    // ────────────────────────────────────────────
    //  web Request → application Command (nested in UseCase)
    // ────────────────────────────────────────────

    public abstract CreatePortfolioUseCase.Command toCreatePortfolioCommand(CreatePortfolioApi.Request request);
    public abstract UpdatePortfolioUseCase.Command toUpdatePortfolioCommand(UpdatePortfolioApi.Request request);

    // sharedkernel Request 컨테이너 nested → sharedkernel Command 컨테이너 nested
    public abstract SharedCommandDto.JobCategory toJobCategoryCommand(SharedRequestApiDto.JobCategory jobCategory);
    public abstract SharedCommandDto.RichTextContent toRichTextContentCommand(SharedRequestApiDto.RichTextContent content);
    public abstract SharedCommandDto.SequentialTag toSequentialTagCommand(SharedRequestApiDto.SequentialTag tag);
    public abstract SharedCommandDto.ExternalLink toExternalLinkCommand(SharedRequestApiDto.ExternalLink link);

    // ────────────────────────────────────────────
    //  application Response (nested in UseCase) → web Response (nested in endpoint 컨테이너)
    // ────────────────────────────────────────────

    // GET /api/portfolios/{id}
    public abstract LoadPortfolioDetailApi.Response toLoadPortfolioDetailApiResponse(LoadPortfolioDetailUseCase.Response response);
    public abstract LoadPortfolioDetailApi.Body toLoadPortfolioDetailApiBody(LoadPortfolioDetailUseCase.Response.Body response);
    public abstract LoadPortfolioDetailApi.Publisher toLoadPortfolioDetailApiPublisher(LoadPortfolioDetailUseCase.Response.Publisher response);
    public abstract LoadPortfolioDetailApi.Publisher.OtherPortfolioSummary toLoadPortfolioDetailApiPublisherOtherPortfolioSummary(LoadPortfolioDetailUseCase.Response.Publisher.OtherPortfolioSummary response);
    public abstract LoadPortfolioDetailApi.LinkedResume toLoadPortfolioDetailApiLinkedResume(LoadPortfolioDetailUseCase.Response.LinkedResume response);
    public abstract LoadPortfolioDetailApi.LinkedCoverLetter toLoadPortfolioDetailApiLinkedCoverLetter(LoadPortfolioDetailUseCase.Response.LinkedCoverLetter response);

    // GET /api/portfolios/{id}/edit
    public abstract LoadPortfolioEditApi.Response toLoadPortfolioEditApiResponse(LoadPortfolioEditUseCase.Response response);
    public abstract LoadPortfolioEditApi.ContentImage toLoadPortfolioEditApiContentImage(LoadPortfolioEditUseCase.Response.ContentImage response);

    // GET /api/portfolios/summaries/mine
    public abstract LoadMyPortfolioSummariesApi.Response toLoadMyPortfolioSummariesApiResponse(LoadMyPortfolioSummariesUseCase.Response response);
    public abstract LoadMyPortfolioSummariesApi.Item toLoadMyPortfolioSummariesApiItem(LoadMyPortfolioSummariesUseCase.Response.Item response);
    public abstract LoadMyPortfolioSummariesApi.LinkedResume toLoadMyPortfolioSummariesApiLinkedResume(LoadMyPortfolioSummariesUseCase.Response.LinkedResume response);
    public abstract LoadMyPortfolioSummariesApi.LinkedCoverLetter toLoadMyPortfolioSummariesApiLinkedCoverLetter(LoadMyPortfolioSummariesUseCase.Response.LinkedCoverLetter response);

    // GET /api/portfolios/summaries/public
    public abstract LoadPublicPortfolioSummariesApi.Response toLoadPublicPortfolioSummariesApiResponse(LoadPublicPortfolioSummariesUseCase.Response response);
    public abstract LoadPublicPortfolioSummariesApi.Item toLoadPublicPortfolioSummariesApiItem(LoadPublicPortfolioSummariesUseCase.Response.Item response);
    public abstract LoadPublicPortfolioSummariesApi.Pagination toLoadPublicPortfolioSummariesApiPagination(LoadPublicPortfolioSummariesUseCase.Response.Pagination response);

    // ────────────────────────────────────────────
    //  application SharedResponseDto → web SharedResponseApiDto
    // ────────────────────────────────────────────

    public abstract SharedResponseApiDto.RichTextContent toRichTextContentApiResponse(SharedResponseDto.RichTextContent response);
    public abstract SharedResponseApiDto.SequentialTag toSequentialTagApiResponse(SharedResponseDto.SequentialTag response);
    public abstract SharedResponseApiDto.ExternalLink toExternalLinkApiResponse(SharedResponseDto.ExternalLink response);
    public abstract SharedResponseApiDto.JobCategory toJobCategoryApiResponse(SharedResponseDto.JobCategory response);
    public abstract SharedResponseApiDto.Thumbnail toThumbnailApiResponse(SharedResponseDto.Thumbnail response);
}

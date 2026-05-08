package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationException;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.CreatePortfolioUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request.CreatePortfolioCommand;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request.PortfolioContentCommand;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.SavePortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadJobCategoryIdByCodePort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.SaveUserStorageUsageLogPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.SaveUserStorageUsagePort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.UpdateUploadStatusOfImageFileMetaPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreatePortfolioUseCaseImpl implements CreatePortfolioUseCase {

    private final UpdateUploadStatusOfImageFileMetaPort updateUploadStatusOfImageFileMetaPort;
    private final SaveUserStorageUsagePort saveUserStorageUsagePort;
    private final SaveUserStorageUsageLogPort saveUserStorageUsageLogPort;
    private final LoadJobCategoryIdByCodePort loadJobCategoryIdByCodePort;
    private final SavePortfolioPort savePortfolioPort;
    private final ObjectMapper objectMapper;

    /**
     *  [기능 개발 항목]
     *    1. ImageFileMeta의 상태 변경
     *    2. UserStorageUsage의 데이터 삽입 (Lazy data insert)
     *        - 데이터가 존재하지 않을 수 있음을 고려
     *        - 데이터가 존재한다면 갱신한다.
     *    3. UserStorageUsageLog의 데이터 삽입
     *    4. Portfolio 데이터 삽입 (sub aggregate: PortfolioContent, PortfolioJobCategory, PortfolioTag)
     *
     *  [로직 플로우]
     *    1. 이미지 파일 메타 상태값의 변경 (소유권 검증 포함)
     *    2. 사용자 스토리지 사용량 저장
     *    3. 카테고리 코드 해석
     *    4. 포트폴리오 데이터의 삽입
     * */
    @Override
    @Transactional
    public Long execute(Long memberId, CreatePortfolioCommand command) {
        List<Long> imageIds = aggregateImageIds(command.thumbnailImageId(), command.contentImageIds());

        updateUploadStatusOfImageFileMetaPort.markUploaded(memberId, imageIds);
        saveUserStorageUsagePort.save(memberId, imageIds);

        Long jobCategoryId = loadJobCategoryIdByCodePort.findIdByCode(command.categoryCode());
        Portfolio portfolio = buildPortfolio(memberId, command, jobCategoryId);

        return savePortfolioPort.save(portfolio).getId();
    }

    private Portfolio buildPortfolio(Long memberId, CreatePortfolioCommand command, Long jobCategoryId) {
        PortfolioContentCommand content = command.content();
        return Portfolio.create(
                memberId,
                jobCategoryId,
                command.customCategory(),
                command.thumbnailImageId(),
                command.linkedCoverLetterId(),
                command.linkedResumeId(),
                command.title(),
                serializeContentJson(content),
                content.html(),
                command.externalLinks(),
                command.privateMemo(),
                command.tags(),
                command.collaborationType(),
                command.visibility()
        );
    }

    private String serializeContentJson(PortfolioContentCommand content) {
        try {
            return objectMapper.writeValueAsString(content.json());
        } catch (JsonProcessingException e) {
            throw new PortfolioApplicationException(
                    PortfolioApplicationExceptionCodeCluster.DetailResponse.CONTENT_JSON_SERIALIZATION_FAILED
            );
        }
    }

    private static List<Long> aggregateImageIds(Long thumbnailImageId, List<Long> contentImageIds) {
        List<Long> imageIds = new ArrayList<>();

        if (thumbnailImageId != null) {
            imageIds.add(thumbnailImageId);
        }

        if (contentImageIds != null && !contentImageIds.isEmpty()) {
            imageIds.addAll(contentImageIds);
        }

        return imageIds;
    }
}

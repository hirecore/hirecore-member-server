package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.CreatePortfolioUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request.CreatePortfolioCommand;
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

    /**
     *  [기능 개발 항목]
     *    1. Portfolio 데이터 삽입 (sub aggregate: PortfolioContent, PortfolioJobCategory, PortfolioTag)
     *    2. ImageFileMeta의 상태 변경
     *    3. UserStorageUsage의 데이터 삽입 (Lazy data insert)
     *        - 데이터가 존재하지 않을 수 있음을 고려
     *        - 데이터가 존재한다면 갱신한다.
     *    4. UserStorageUsageLog의 데이터 삽입
     *
     *  [로직 플로우]
     *    1. 사용자 스토리지 데이터 저장
     *    2. 이미지 파일 메타 상태값의 변경
     *    3. 포트폴리오 데이터의 삽입
     * */
    @Override
    @Transactional
    public void execute(Long memberId, CreatePortfolioCommand command) {
        List<Long> imageIds = aggregateImageIds(command.thumbnailImageId(), command.contentImageIds());

        updateUploadStatusOfImageFileMetaPort.markUploaded(memberId, imageIds);
        saveUserStorageUsagePort.save(memberId, imageIds);
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

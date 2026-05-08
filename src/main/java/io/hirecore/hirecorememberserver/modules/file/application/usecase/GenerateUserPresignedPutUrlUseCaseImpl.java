package io.hirecore.hirecorememberserver.modules.file.application.usecase;

import io.hirecore.hirecorememberserver.modules.file.application.exception.FileApplicationException;
import io.hirecore.hirecorememberserver.modules.file.application.exception.FileApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.file.application.port.in.GenerateUserPresignedPutUrlUseCase;
import io.hirecore.hirecorememberserver.modules.file.application.port.in.dto.request.ImagePresignedPutUrlCommand;
import io.hirecore.hirecorememberserver.modules.file.application.port.in.dto.response.ImagePresignedPutUrlResponse;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.GeneratePresignedPutUrlPort;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.SaveImageFileMetaPort;
import io.hirecore.hirecorememberserver.modules.file.application.util.ImageObjectKeyResolver;
import io.hirecore.hirecorememberserver.modules.file.domain.ImageFileMeta;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadUserStorageLimitPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadUserStorageUsagePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GenerateUserPresignedPutUrlUseCaseImpl implements GenerateUserPresignedPutUrlUseCase {

    private final LoadUserStorageLimitPort loadUserStorageLimitPort;
    private final LoadUserStorageUsagePort loadUserStorageUsagePort;
    private final GeneratePresignedPutUrlPort generatePresignedPutUrlPort;
    private final SaveImageFileMetaPort saveImageFileMetaPort;

    @Override
    public List<ImagePresignedPutUrlResponse> execute(
            Long memberAccountId,
            List<ImagePresignedPutUrlCommand> commandList
    ) {
        Long storageSnapshotBytes = loadUserStorageLimitPort.getStorageLimitBytes(memberAccountId);
        Long usedStorageBytes = loadUserStorageUsagePort.getUsedQuotaBytes(memberAccountId);

        verifyUploadEligibility(commandList, storageSnapshotBytes, usedStorageBytes);

        return commandList.stream()
                .map(command -> generatePresignedPutUrlResponse(memberAccountId, command))
                .toList();
    }

    private ImagePresignedPutUrlResponse generatePresignedPutUrlResponse(
            Long memberAccountId,
            ImagePresignedPutUrlCommand command
    ) {
        String objectKey = buildObjectKey(memberAccountId, command);

        ImageFileMeta imageFileMeta = saveImageFileMetaPort.save(
                ImageFileMeta.create(
                        memberAccountId,
                        command.domainType(),
                        command.purpose(),
                        "AWS_S3",
                        generatePresignedPutUrlPort.getBucketName(),
                        objectKey,
                        command.originalFileName(),
                        command.mimeType(),
                        command.fileExtension(),
                        command.fileSizeBytes(),
                        command.width(),
                        command.height()
                )
        );

        String presignedUrl = generatePresignedPutUrlPort.generate(
                objectKey,
                command.mimeType().getValue(),
                command.fileSizeBytes()
        );

        String publicUrl = generatePresignedPutUrlPort.getPublicBaseUrl() + "/" + objectKey;

        return new ImagePresignedPutUrlResponse(
                command.clientFileId(),
                imageFileMeta.getId(),
                presignedUrl,
                publicUrl
        );
    }

    /*
     * [object key 경로 생성 응답]
     *      - users/{memberAccountId}/{domainType}/{purpose}/{UUID}.{fileExtension}
     * */
    private static String buildObjectKey(Long memberAccountId, ImagePresignedPutUrlCommand command) {
        return String.join("/",
                "users",
                String.valueOf(memberAccountId),
                ImageObjectKeyResolver.pathSegmentOf(command.domainType()),
                ImageObjectKeyResolver.pathSegmentOf(command.purpose()),
                UUID.randomUUID() + "." + command.fileExtension().name().toLowerCase()
        );
    }

    /*
    *   [검증 항목]
    *       1. 요청 파일 크기 합계가 스토리지 잔여 용량을 초과하는지 확인
    *       2. 이미 사용된 스토리지 용량과 합산하여 스토리지 잔여 용량을 초과하는지 확인
    *
    *   [특이사항]
    *       1. MIME 타입 검증: MIME 타입에 검증의 경우 요청된 JSON이 역직렬화 될 때 검증진행
    *           - application layer에서도 MIME 타입 검증을 진행해야 하지 않는가에 대한 고민이 필요.
    * */
    private static void verifyUploadEligibility(
            List<ImagePresignedPutUrlCommand> commandList,
            Long storageSnapshotBytes,
            Long usedStorageBytes
    ) {
        Long requestBytesSum = commandList.stream()
                .mapToLong(ImagePresignedPutUrlCommand::fileSizeBytes)
                .sum();

        if (requestBytesSum + usedStorageBytes > storageSnapshotBytes) {
            throw new FileApplicationException(
                    FileApplicationExceptionCodeCluster.DetailResponse.STORAGE_QUOTA_EXCEEDED
            );
        }
    }
}

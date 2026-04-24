package io.hirecore.hirecorememberserver.modules.file.application.usecase;

import io.hirecore.hirecorememberserver.modules.file.application.exception.FileApplicationException;
import io.hirecore.hirecorememberserver.modules.file.application.exception.FileApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.file.application.port.in.GenerateUserPresignedPutUrlUseCase;
import io.hirecore.hirecorememberserver.modules.file.application.port.in.dto.request.ImagePresignedPutUrlCommand;
import io.hirecore.hirecorememberserver.modules.file.application.port.in.dto.response.ImagePresignedPutUrlResponse;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.GeneratePresignedPutUrlPort;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.SaveImageFileMetaPort;
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
        Long storageSnapshotBytes = loadUserStorageLimitPort.getBytes(memberAccountId);
        Long usedStorageBytes = loadUserStorageUsagePort.getBytes(memberAccountId);

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

    private static String buildObjectKey(Long memberAccountId, ImagePresignedPutUrlCommand command) {
        return String.join("/",
                "users",
                String.valueOf(memberAccountId),
                command.domainType().getPathSegment(),
                command.purpose().getPathSegment(),
                UUID.randomUUID() + "." + command.fileExtension().name().toLowerCase()
        );
    }

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

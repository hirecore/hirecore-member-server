package io.hirecore.hirecorememberserver.modules.file.application.usecase;

import io.hirecore.hirecorememberserver.modules.file.application.port.in.IssueImageUploadUrlUseCase;
import io.hirecore.hirecorememberserver.modules.file.application.port.in.dto.request.ImagePresignedPutUrlCommand;
import io.hirecore.hirecorememberserver.modules.file.application.port.in.dto.response.ImagePresignedPutUrlResponse;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.GeneratePresignedPutUrlPort;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.SaveImageFileMetaPort;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.dto.PresignedPutUrl;
import io.hirecore.hirecorememberserver.modules.file.application.util.ImageObjectKeyResolver;
import io.hirecore.hirecorememberserver.modules.file.domain.ImageFileMeta;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.VerifyUserStorageCapacityPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IssueImageUploadUrlUseCaseImpl implements IssueImageUploadUrlUseCase {

    private final VerifyUserStorageCapacityPort verifyUserStorageCapacityPort;
    private final GeneratePresignedPutUrlPort generatePresignedPutUrlPort;
    private final SaveImageFileMetaPort saveImageFileMetaPort;

    @Override
    public List<ImagePresignedPutUrlResponse> execute(
            Long memberAccountId,
            List<ImagePresignedPutUrlCommand> commandList
    ) {
        Long requestBytesSum = commandList.stream()
                .mapToLong(ImagePresignedPutUrlCommand::fileSizeBytes)
                .sum();

        verifyUserStorageCapacityPort.verifyCapacityFor(memberAccountId, requestBytesSum);

        return commandList.stream()
                .map(command -> issueUploadUrl(memberAccountId, command))
                .toList();
    }

    private ImagePresignedPutUrlResponse issueUploadUrl(
            Long memberAccountId,
            ImagePresignedPutUrlCommand command
    ) {
        String objectKey = buildObjectKey(memberAccountId, command);

        PresignedPutUrl presignedPutUrl = generatePresignedPutUrlPort.generate(
                objectKey,
                command.mimeType().getValue(),
                command.fileSizeBytes()
        );

        ImageFileMeta imageFileMeta = saveImageFileMetaPort.save(
                ImageFileMeta.create(
                        memberAccountId,
                        command.domainType(),
                        command.purpose(),
                        "AWS_S3",
                        presignedPutUrl.bucketName(),
                        objectKey,
                        command.originalFileName(),
                        command.mimeType(),
                        command.fileExtension(),
                        command.fileSizeBytes(),
                        command.width(),
                        command.height()
                )
        );

        return new ImagePresignedPutUrlResponse(
                command.clientFileId(),
                imageFileMeta.getId(),
                presignedPutUrl.presignedUrl(),
                presignedPutUrl.publicUrl()
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
}

package io.hirecore.hirecorememberserver.modules.file.application.usecase;

import io.hirecore.hirecorememberserver.modules.file.application.port.in.IssueImageUploadUrlUseCase;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.GeneratePresignedPutUrlPort;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.SaveImageFileMetaPort;
import io.hirecore.hirecorememberserver.modules.file.application.util.ImageObjectKeyResolver;
import io.hirecore.hirecorememberserver.modules.file.domain.ImageFileMeta;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.VerifyUserStorageCapacitySharedPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IssueImageUploadUrlUseCaseImpl implements IssueImageUploadUrlUseCase {

    private final VerifyUserStorageCapacitySharedPort verifyUserStorageCapacitySharedPort;
    private final GeneratePresignedPutUrlPort generatePresignedPutUrlPort;
    private final SaveImageFileMetaPort saveImageFileMetaPort;

    @Override
    public List<IssueImageUploadUrlUseCase.Response> execute(
            Long memberAccountId,
            List<IssueImageUploadUrlUseCase.Command> commandList
    ) {
        Long requestBytesSum = commandList.stream()
                .mapToLong(IssueImageUploadUrlUseCase.Command::fileSizeBytes)
                .sum();

        verifyUserStorageCapacitySharedPort.verifyCapacityFor(memberAccountId, requestBytesSum);

        return commandList.stream()
                .map(command -> issueUploadUrl(memberAccountId, command))
                .toList();
    }

    private IssueImageUploadUrlUseCase.Response issueUploadUrl(
            Long memberAccountId,
            IssueImageUploadUrlUseCase.Command command
    ) {
        String objectKey = buildObjectKey(memberAccountId, command);

        GeneratePresignedPutUrlPort.Result presignedPutUrl = generatePresignedPutUrlPort.generate(
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

        return new IssueImageUploadUrlUseCase.Response(
                command.clientFileId(),
                imageFileMeta.getId(),
                presignedPutUrl.presignedUrl(),
                presignedPutUrl.publicUrl()
        );
    }

    // users/{memberAccountId}/{domainType}/{purpose}/{UUID}.{fileExtension}
    private static String buildObjectKey(Long memberAccountId, IssueImageUploadUrlUseCase.Command command) {
        return String.join("/",
                "users",
                String.valueOf(memberAccountId),
                ImageObjectKeyResolver.pathSegmentOf(command.domainType()),
                ImageObjectKeyResolver.pathSegmentOf(command.purpose()),
                UUID.randomUUID() + "." + command.fileExtension().getValue()
        );
    }
}

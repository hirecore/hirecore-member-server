package io.hirecore.hirecorememberserver.modules.file.application.port.in;

import io.hirecore.hirecorememberserver.modules.file.application.port.in.dto.request.ImagePresignedPutUrlCommand;
import io.hirecore.hirecorememberserver.modules.file.application.port.in.dto.response.ImagePresignedPutUrlResponse;

import java.util.List;

public interface IssueImageUploadUrlUseCase {
    List<ImagePresignedPutUrlResponse> execute(Long memberAccountId, List<ImagePresignedPutUrlCommand> commandList);
}

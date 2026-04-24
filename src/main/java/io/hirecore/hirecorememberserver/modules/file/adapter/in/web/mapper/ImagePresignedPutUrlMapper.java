package io.hirecore.hirecorememberserver.modules.file.adapter.in.web.mapper;

import io.hirecore.hirecorememberserver.common.mapper.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.file.adapter.in.web.dto.request.ImagePresignedPutUrlApiRequest;
import io.hirecore.hirecorememberserver.modules.file.adapter.in.web.dto.response.ImagePresignedPutUrlApiResponse;
import io.hirecore.hirecorememberserver.modules.file.application.port.in.dto.request.ImagePresignedPutUrlCommand;
import io.hirecore.hirecorememberserver.modules.file.application.port.in.dto.response.ImagePresignedPutUrlResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = GlobalMapStructConfig.class)
public abstract class ImagePresignedPutUrlMapper {
    // ApiRequest -> Command
    public abstract ImagePresignedPutUrlCommand toCommand(ImagePresignedPutUrlApiRequest request);
    public abstract List<ImagePresignedPutUrlCommand> toCommandList(List<ImagePresignedPutUrlApiRequest> requestList);

    // ApplicationResponse -> ApiResponse
    public abstract ImagePresignedPutUrlApiResponse toResponse(ImagePresignedPutUrlResponse response);
    public abstract List<ImagePresignedPutUrlApiResponse> toResponseList(List<ImagePresignedPutUrlResponse> responseList);
}
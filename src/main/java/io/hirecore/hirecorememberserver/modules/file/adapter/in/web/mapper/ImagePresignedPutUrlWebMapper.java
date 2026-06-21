package io.hirecore.hirecorememberserver.modules.file.adapter.in.web.mapper;

import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.file.adapter.in.web.dto.ImagePresignedPutUrlApi;
import io.hirecore.hirecorememberserver.modules.file.application.port.in.dto.request.ImagePresignedPutUrlCommand;
import io.hirecore.hirecorememberserver.modules.file.application.port.in.dto.response.ImagePresignedPutUrlResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = GlobalMapStructConfig.class)
public abstract class ImagePresignedPutUrlWebMapper {
    // ApiRequest -> Command
    public abstract ImagePresignedPutUrlCommand toCommand(ImagePresignedPutUrlApi.Request request);
    public abstract List<ImagePresignedPutUrlCommand> toCommandList(List<ImagePresignedPutUrlApi.Request> requestList);

    // ApplicationResponse -> ApiResponse
    public abstract ImagePresignedPutUrlApi.Response toResponse(ImagePresignedPutUrlResponse response);
    public abstract List<ImagePresignedPutUrlApi.Response> toResponseList(List<ImagePresignedPutUrlResponse> responseList);
}
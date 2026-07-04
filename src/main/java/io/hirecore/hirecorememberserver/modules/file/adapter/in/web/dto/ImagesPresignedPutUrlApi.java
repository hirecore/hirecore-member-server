package io.hirecore.hirecorememberserver.modules.file.adapter.in.web.dto;

import jakarta.validation.Valid;

import java.util.List;

// 이미지 다건 presigned PUT URL 발급 web contract
public class ImagesPresignedPutUrlApi {

    private ImagesPresignedPutUrlApi() {}

    public record Request(
            @Valid List<ImagePresignedPutUrlApi.Request> files
    ) {}

    public record Response(
            List<ImagePresignedPutUrlApi.Response> files
    ) {}
}

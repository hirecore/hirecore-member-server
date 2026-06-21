package io.hirecore.hirecorememberserver.modules.file.adapter.in.web;

import io.hirecore.hirecorememberserver.sharedkernel.application.security.AuthPrincipal;
import io.hirecore.hirecorememberserver.modules.file.adapter.in.web.dto.ImagesPresignedPutUrlApi;
import io.hirecore.hirecorememberserver.modules.file.adapter.in.web.dto.ImagePresignedPutUrlApi;
import io.hirecore.hirecorememberserver.modules.file.adapter.in.web.mapper.ImagePresignedPutUrlWebMapper;
import io.hirecore.hirecorememberserver.modules.file.application.port.in.IssueImageUploadUrlUseCase;
import io.hirecore.hirecorememberserver.modules.file.application.port.in.dto.request.ImagePresignedPutUrlCommand;
import io.hirecore.hirecorememberserver.modules.file.application.port.in.dto.response.ImagePresignedPutUrlResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users/files")
@RequiredArgsConstructor
public class UserFileCommandController {

    private final ImagePresignedPutUrlWebMapper imagePresignedPutUrlWebMapper;
    private final IssueImageUploadUrlUseCase issueImageUploadUrlUseCase;

    @PostMapping("/images/presigned-put-url")
    public ResponseEntity<ImagesPresignedPutUrlApi.Response> postImagesPresignedPutUrl(
            @AuthenticationPrincipal AuthPrincipal authPrincipal,
            @Valid @RequestBody ImagesPresignedPutUrlApi.Request request
    ) {
        List<ImagePresignedPutUrlCommand> commandList = imagePresignedPutUrlWebMapper.toCommandList(request.files());
        List<ImagePresignedPutUrlResponse> applicationResponseList = issueImageUploadUrlUseCase.execute(authPrincipal.id(), commandList);
        List<ImagePresignedPutUrlApi.Response> apiResponseList = imagePresignedPutUrlWebMapper.toResponseList(applicationResponseList);

        return ResponseEntity.ok().body(new ImagesPresignedPutUrlApi.Response(apiResponseList));
    }
}

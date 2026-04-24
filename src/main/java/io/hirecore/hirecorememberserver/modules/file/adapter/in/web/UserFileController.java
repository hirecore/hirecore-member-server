package io.hirecore.hirecorememberserver.modules.file.adapter.in.web;

import io.hirecore.hirecorememberserver.sharedkernel.application.security.AuthPrincipal;
import io.hirecore.hirecorememberserver.modules.file.adapter.in.web.dto.request.ImagesPresignedPutUrlApiRequest;
import io.hirecore.hirecorememberserver.modules.file.adapter.in.web.dto.response.ImagePresignedPutUrlApiResponse;
import io.hirecore.hirecorememberserver.modules.file.adapter.in.web.dto.response.ImagesPresignedPutUrlApiResponse;
import io.hirecore.hirecorememberserver.modules.file.adapter.in.web.mapper.ImagePresignedPutUrlMapper;
import io.hirecore.hirecorememberserver.modules.file.application.port.in.GenerateUserPresignedPutUrlUseCase;
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
public class UserFileController {

    private final ImagePresignedPutUrlMapper imagePresignedPutUrlMapper;
    private final GenerateUserPresignedPutUrlUseCase generateUserPresignedPutUrlUseCase;

    @PostMapping("/images/presigned-url")
    public ResponseEntity<ImagesPresignedPutUrlApiResponse> postImagesPresignedUrl(
            @AuthenticationPrincipal AuthPrincipal authPrincipal,
            @Valid @RequestBody ImagesPresignedPutUrlApiRequest request
    ) {
        List<ImagePresignedPutUrlCommand> commandList = imagePresignedPutUrlMapper.toCommandList(request.files());
        List<ImagePresignedPutUrlResponse> applicationResponseList = generateUserPresignedPutUrlUseCase.execute(authPrincipal.id(), commandList);
        List<ImagePresignedPutUrlApiResponse> apiResponseList = imagePresignedPutUrlMapper.toResponseList(applicationResponseList);

        return ResponseEntity.ok().body(new ImagesPresignedPutUrlApiResponse(apiResponseList));
    }
}

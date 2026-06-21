package io.hirecore.hirecorememberserver.modules.file.adapter.in.web;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hirecore.hirecorememberserver.common.web.SwaggerDocs;
import io.hirecore.hirecorememberserver.common.security.WebMvcSecuritySupport;
import io.hirecore.hirecorememberserver.common.config.StrictJsonConfig;
import io.hirecore.hirecorememberserver.sharedkernel.application.security.AuthPrincipal;
import io.hirecore.hirecorememberserver.modules.file.adapter.in.web.mapper.ImagePresignedPutUrlWebMapperImpl;
import io.hirecore.hirecorememberserver.modules.file.application.exception.FileApplicationException;
import io.hirecore.hirecorememberserver.modules.file.application.exception.FileApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.file.application.port.in.GenerateUserPresignedPutUrlUseCase;
import io.hirecore.hirecorememberserver.modules.file.application.port.in.dto.response.ImagePresignedPutUrlResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("UserFileCommandController 웹 계층 테스트")
@WebMvcTest(controllers = UserFileCommandController.class)
@Import({
        WebMvcSecuritySupport.class,
        StrictJsonConfig.class,
        ImagePresignedPutUrlWebMapperImpl.class
})
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
class UserFileCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GenerateUserPresignedPutUrlUseCase generateUserPresignedPutUrlUseCase;

    private static final Long MEMBER_ACCOUNT_ID = 1L;

    @BeforeEach
    void setUpAuthentication() {
        AuthPrincipal principal = new AuthPrincipal(MEMBER_ACCOUNT_ID, "user@example.com", "USER", 0);
        var auth = new UsernamePasswordAuthenticationToken(principal, "token", principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private String createValidRequestBody() throws Exception {
        return objectMapper.writeValueAsString(Map.of(
                "files", List.of(
                        Map.of(
                                "clientFileId", 1,
                                "originalFileName", "my-image.webp",
                                "mimeType", "image/webp",
                                "fileExtension", "webp",
                                "fileSizeBytes", 1048576,
                                "width", 1920,
                                "height", 1080,
                                "domainType", "portfolio",
                                "purpose", "contentImage"
                        )
                )
        ));
    }

    // ──────────────────────────────────────────────
    //  Presigned PUT URL 발급 테스트
    // ──────────────────────────────────────────────

    @Nested
    @DisplayName("Presigned PUT URL 발급: 성공 케이스 (Happy Path)")
    class PresignedPutUrlSuccessTest {

        @Test
        @DisplayName("[200 OK] 올바른 요청 시 presigned URL과 public URL을 반환한다.")
        void presigned_put_url_success() throws Exception {
            // given
            String requestBody = createValidRequestBody();

            ImagePresignedPutUrlResponse useCaseResponse = new ImagePresignedPutUrlResponse(
                    1L,
                    100L,
                    "https://s3.ap-northeast-2.amazonaws.com/test-bucket/users/1/portfolio/content-image/uuid.webp?X-Amz-Signature=...",
                    "https://cdn.hirecore.io/users/1/portfolio/content-image/uuid.webp"
            );

            given(generateUserPresignedPutUrlUseCase.execute(eq(MEMBER_ACCOUNT_ID), any()))
                    .willReturn(List.of(useCaseResponse));

            // when & then
            mockMvc.perform(post("/api/users/files/images/presigned-put-url")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.files").isArray())
                    .andExpect(jsonPath("$.files[0].clientFileId").value("1"))
                    .andExpect(jsonPath("$.files[0].imageFileMetaId").value("100"))
                    .andExpect(jsonPath("$.files[0].presignedUrl").isString())
                    .andExpect(jsonPath("$.files[0].publicUrl").isString())

                    // 문서화
                    .andDo(document("200-user-file-presigned-put-url-success",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag(SwaggerDocs.Tags.File.USER_FILE)
                                            .summary("\"이미지 업로드 Presigned URL 발급\": 사용자 이미지 업로드를 위한 Presigned PUT URL을 발급한다.")
                                            .description("""
                                                    클라이언트가 S3에 직접 이미지를 업로드할 수 있도록 Presigned PUT URL을 발급합니다.

                                                        [처리 흐름]
                                                         1. 사용자의 스토리지 할당량과 현재 사용량을 조회합니다.
                                                         2. 요청된 파일들의 총 크기가 가용 용량을 초과하지 않는지 검증합니다.
                                                         3. 파일 메타데이터를 PENDING 상태로 DB에 저장합니다.
                                                         4. S3 Presigned PUT URL과 CDN Public URL을 발급합니다.

                                                        [응답 방식]
                                                         - 200 OK: 각 파일에 대한 presignedUrl, publicUrl, imageFileMetaId를 반환합니다.

                                                        [에러 응답]
                                                         - 400 STORAGE_QUOTA_EXCEEDED: 가용 스토리지 용량 초과
                                                         - 400 UNSUPPORTED_MIME_TYPE: 지원하지 않는 파일 형식
                                                         - 400 REQUEST_VALUE_INVALID: 필수 필드 누락

                                                        [특이 사항]
                                                         - Presigned URL은 설정된 만료 시간 내에만 유효합니다.
                                                         - 업로드 완료 후 별도의 콜백으로 상태를 COMPLETED로 변경해야 합니다.
                                                    """)
                                            .requestFields(
                                                    fieldWithPath("files")
                                                            .type(JsonFieldType.ARRAY)
                                                            .description("업로드할 이미지 파일 목록"),
                                                    fieldWithPath("files[].clientFileId")
                                                            .type(JsonFieldType.NUMBER)
                                                            .description("클라이언트에서 부여한 파일 식별자 (응답 매칭용)"),
                                                    fieldWithPath("files[].originalFileName")
                                                            .type(JsonFieldType.STRING)
                                                            .description("원본 파일명"),
                                                    fieldWithPath("files[].mimeType")
                                                            .type(JsonFieldType.STRING)
                                                            .description("MIME 타입 (지원: image/webp)"),
                                                    fieldWithPath("files[].fileExtension")
                                                            .type(JsonFieldType.STRING)
                                                            .description("파일 확장자 (지원: WebP)"),
                                                    fieldWithPath("files[].fileSizeBytes")
                                                            .type(JsonFieldType.NUMBER)
                                                            .description("파일 크기 (바이트)"),
                                                    fieldWithPath("files[].width")
                                                            .type(JsonFieldType.NUMBER)
                                                            .description("이미지 가로 해상도 (픽셀)"),
                                                    fieldWithPath("files[].height")
                                                            .type(JsonFieldType.NUMBER)
                                                            .description("이미지 세로 해상도 (픽셀)"),
                                                    fieldWithPath("files[].domainType")
                                                            .type(JsonFieldType.STRING)
                                                            .description("파일이 소속된 도메인 유형 (PORTFOLIO, RESUME, COVER_LETTER)"),
                                                    fieldWithPath("files[].purpose")
                                                            .type(JsonFieldType.STRING)
                                                            .description("파일 사용 목적 (CONTENT_IMAGE, THUMBNAIL_IMAGE)")
                                            )
                                            .responseFields(
                                                    fieldWithPath("files")
                                                            .type(JsonFieldType.ARRAY)
                                                            .description("발급된 Presigned URL 목록"),
                                                    fieldWithPath("files[].clientFileId")
                                                            .type(JsonFieldType.STRING)
                                                            .description("요청 시 전달한 클라이언트 파일 식별자"),
                                                    fieldWithPath("files[].imageFileMetaId")
                                                            .type(JsonFieldType.STRING)
                                                            .description("서버에서 생성된 이미지 파일 메타 ID (TSID 정밀도 보존을 위해 문자열로 직렬화)"),
                                                    fieldWithPath("files[].presignedUrl")
                                                            .type(JsonFieldType.STRING)
                                                            .description("S3 Presigned PUT URL (이 URL로 파일을 업로드)"),
                                                    fieldWithPath("files[].publicUrl")
                                                            .type(JsonFieldType.STRING)
                                                            .description("CDN Public URL (업로드 완료 후 이 URL로 접근)")
                                            )
                                            .build()
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("Presigned PUT URL 발급: 비즈니스 로직 실패 케이스")
    class PresignedPutUrlBusinessFailureTest {

        @Test
        @DisplayName("[400 Bad Request] 스토리지 용량 초과 시 STORAGE_QUOTA_EXCEEDED 에러를 반환한다.")
        void presigned_put_url_storage_quota_exceeded() throws Exception {
            // given
            String requestBody = createValidRequestBody();

            given(generateUserPresignedPutUrlUseCase.execute(eq(MEMBER_ACCOUNT_ID), any()))
                    .willThrow(new FileApplicationException(
                            FileApplicationExceptionCodeCluster.DetailResponse.STORAGE_QUOTA_EXCEEDED
                    ));

            // when & then
            mockMvc.perform(post("/api/users/files/images/presigned-put-url")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("STORAGE_QUOTA_EXCEEDED"))
                    .andExpect(jsonPath("$.message").value("저장 공간이 부족합니다. 기존 파일을 삭제하거나 멤버십을 업그레이드해주세요."))

                    // 문서화
                    .andDo(document("400-user-file-presigned-put-url-storage-exceeded",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag(SwaggerDocs.Tags.File.USER_FILE)
                                            .responseFields(
                                                    fieldWithPath("timestamp")
                                                            .type(JsonFieldType.STRING)
                                                            .description("에러 발생 시각 (ISO-8601 형식)"),
                                                    fieldWithPath("trackingId")
                                                            .type(JsonFieldType.STRING)
                                                            .description("에러 추적용 고유 ID"),
                                                    fieldWithPath("errorCode")
                                                            .type(JsonFieldType.STRING)
                                                            .description("에러 코드: STORAGE_QUOTA_EXCEEDED"),
                                                    fieldWithPath("message")
                                                            .type(JsonFieldType.STRING)
                                                            .description("사용자 안내 메시지"),
                                                    fieldWithPath("path")
                                                            .type(JsonFieldType.STRING)
                                                            .description("에러가 발생한 요청 URI 경로"),
                                                    fieldWithPath("attributes")
                                                            .type(JsonFieldType.OBJECT)
                                                            .description("부가 데이터")
                                                            .optional()
                                            )
                                            .build()
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("Presigned PUT URL 발급: 입력값 검증 실패 케이스")
    class PresignedPutUrlValidationFailureTest {

        @Test
        @DisplayName("[400 Bad Request] 필수 필드가 누락되면 검증 실패 에러를 반환한다.")
        void presigned_put_url_validation_missing_required_fields() throws Exception {
            // given
            String requestBody = objectMapper.writeValueAsString(Map.of(
                    "files", List.of(
                            Map.of(
                                    "originalFileName", "my-image.webp"
                            )
                    )
            ));

            // when & then
            mockMvc.perform(post("/api/users/files/images/presigned-put-url")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").exists());
        }

        @Test
        @DisplayName("[400 Bad Request] 지원하지 않는 MimeType을 전달하면 에러를 반환한다.")
        void presigned_put_url_validation_unsupported_mime_type() throws Exception {
            // given
            String requestBody = objectMapper.writeValueAsString(Map.of(
                    "files", List.of(
                            Map.of(
                                    "clientFileId", 1,
                                    "originalFileName", "my-image.png",
                                    "mimeType", "image/png",
                                    "fileExtension", "webp",
                                    "fileSizeBytes", 1048576,
                                    "width", 1920,
                                    "height", 1080,
                                    "domainType", "portfolio",
                                    "purpose", "contentImage"
                            )
                    )
            ));

            // when & then
            mockMvc.perform(post("/api/users/files/images/presigned-put-url")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").exists());
        }
    }
}

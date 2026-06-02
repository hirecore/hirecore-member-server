package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web;

import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.SimpleType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hirecore.hirecorememberserver.common.config.StrictJsonConfig;
import io.hirecore.hirecorememberserver.common.security.WebMvcSecuritySupport;
import io.hirecore.hirecorememberserver.common.web.SwaggerDocs;
import io.hirecore.hirecorememberserver.modules.category.application.exception.CategoryApplicationException;
import io.hirecore.hirecorememberserver.modules.category.application.exception.CategoryApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.file.application.exception.FileApplicationException;
import io.hirecore.hirecorememberserver.modules.file.application.exception.FileApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.file.domain.exception.ImageFileMetaDomainException;
import io.hirecore.hirecorememberserver.modules.file.domain.exception.ImageFileMetaDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.mapper.PortfolioWebMapperImpl;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationException;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.CreatePortfolioUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadPortfolioDetailUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadPortfolioEditUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.UpdatePortfolioUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response.PortfolioContentResponse;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response.PortfolioDetailResponse;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response.PortfolioEditResponse;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response.PortfolioJobCategoryResponse;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response.PortfolioTagResponse;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.mapper.SharedDomainVoWebMapperImpl;
import io.hirecore.hirecorememberserver.sharedkernel.application.security.AuthPrincipal;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.CollaborationType;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response.PortfolioExternalLinkResponse;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;
import java.time.Instant;
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
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("PortfolioController 웹 계층 테스트")
@WebMvcTest(controllers = PortfolioController.class)
@Import({
        WebMvcSecuritySupport.class,
        StrictJsonConfig.class,
        PortfolioWebMapperImpl.class,
        SharedDomainVoWebMapperImpl.class
})
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
class PortfolioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreatePortfolioUseCase createPortfolioUseCase;

    @MockitoBean
    private LoadPortfolioDetailUseCase loadPortfolioDetailUseCase;

    @MockitoBean
    private LoadPortfolioEditUseCase loadPortfolioEditUseCase;

    @MockitoBean
    private UpdatePortfolioUseCase updatePortfolioUseCase;

    private static final Long MEMBER_ACCOUNT_ID = 1L;
    private static final Long CREATED_PORTFOLIO_ID = 9001L;

    @BeforeEach
    void setUpAuthentication() {
        AuthPrincipal principal = new AuthPrincipal(MEMBER_ACCOUNT_ID, "user@example.com", "USER", 0);
        var auth = new UsernamePasswordAuthenticationToken(principal, "token", principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private String createValidRequestBody() throws Exception {
        return objectMapper.writeValueAsString(Map.ofEntries(
                Map.entry("jobCategory", Map.of(
                        "code", "DEV_BACKEND",
                        "userInput", "백엔드 직무"
                )),
                Map.entry("collaborationType", "team"),
                Map.entry("visibility", "public"),
                Map.entry("title", "회원 서비스 도메인 모델링 회고"),
                Map.entry("privateMemo", "회고 작성 시 참고용 메모입니다."),
                Map.entry("previewSummary", "회원 서비스를 도메인 모델링한 회고를 정리한 글입니다."),
                Map.entry("thumbnailImageId", "100"),
                Map.entry("contentImageIds", List.of("101", "102")),
                Map.entry("tags", List.of(
                        Map.of("userInputTag", "Spring",    "sortOrder", 0),
                        Map.of("userInputTag", "DDD",       "sortOrder", 1),
                        Map.of("userInputTag", "Hexagonal", "sortOrder", 2)
                )),
                Map.entry("externalLinks", List.of(
                        Map.of("label", "GitHub Repo", "url", "https://github.com/example/repo"),
                        Map.of("label", "데모", "url", "https://demo.example.com")
                )),
                Map.entry("content", Map.of(
                        "json", Map.of("type", "doc", "content", List.of()),
                        "html", "<p>포트폴리오 본문 HTML 입니다.</p>"
                )),
                Map.entry("linkedResumeId", "7001"),
                Map.entry("linkedCoverLetterId", "8001")
        ));
    }

    // ──────────────────────────────────────────────
    //  포트폴리오 등록: 성공 케이스
    // ──────────────────────────────────────────────

    @Nested
    @DisplayName("포트폴리오 등록: 성공 케이스 (Happy Path)")
    class CreatePortfolioSuccessTest {

        @Test
        @DisplayName("[201 Created] 올바른 요청 시 portfolioId를 반환하고 Location 헤더에 리소스 위치를 담는다.")
        void create_portfolio_success() throws Exception {
            // given
            String requestBody = createValidRequestBody();
            given(createPortfolioUseCase.execute(eq(MEMBER_ACCOUNT_ID), any()))
                    .willReturn(CREATED_PORTFOLIO_ID);

            // when & then
            mockMvc.perform(post("/api/portfolios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/api/portfolios/" + CREATED_PORTFOLIO_ID)))
                    .andExpect(jsonPath("$.portfolioId").value(String.valueOf(CREATED_PORTFOLIO_ID)))

                    // 문서화
                    .andDo(document("201-portfolio-create-success",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag(SwaggerDocs.Tags.Portfolio.PORTFOLIO)
                                            .summary("\"포트폴리오 등록\": 사용자 포트폴리오를 새로 생성한다.")
                                            .description("""
                                                    사용자가 작성한 포트폴리오 본문/메타데이터를 등록합니다.

                                                        [처리 흐름]
                                                         1. 요청에 포함된 이미지 식별자(썸네일, 본문 이미지)의 소유권을 검증하고 UPLOADED 상태로 전이합니다.
                                                            전이 시 ImageUploadedEvent가 발행되어, 트랜잭션 커밋 이후 storage BC가 사용량을 갱신합니다.
                                                         2. jobCategory.code 를 직무 카테고리 ID로 해석합니다.
                                                         3. Portfolio 도메인을 생성하고 영속화합니다.

                                                        [응답 방식]
                                                         - 201 Created: 생성된 portfolioId와 Location 헤더(`/api/portfolios/{portfolioId}`)를 반환합니다.

                                                        [에러 응답]
                                                         - 400 REQUEST_VALUE_INVALID: 요청 본문 필수 필드 누락
                                                         - 403 IMAGE_OWNERSHIP_VIOLATION: 다른 사용자 소유 이미지 사용 시도
                                                         - 404 IMAGE_NOT_FOUND: 존재하지 않는 imageFileMetaId 참조
                                                         - 404 JOB_CATEGORY_CODE_NOT_FOUND: 유효하지 않은 jobCategory.code
                                                         - 409 INVALID_UPLOAD_STATUS_TRANSITION: 이미지가 PENDING 상태가 아님
                                                    """)
                                            .requestFields(
                                                    fieldWithPath("jobCategory")
                                                            .type(JsonFieldType.OBJECT)
                                                            .description("직무 카테고리 선택 정보"),
                                                    fieldWithPath("jobCategory.code")
                                                            .type(JsonFieldType.STRING)
                                                            .description("직무 카테고리 코드 (예: DEV_BACKEND)"),
                                                    fieldWithPath("jobCategory.userInput")
                                                            .type(JsonFieldType.STRING)
                                                            .description("사용자가 입력한 포트폴리오 카테고리 라벨 (allowsCustomInput 카테고리에서만 입력)")
                                                            .optional(),
                                                    fieldWithPath("collaborationType")
                                                            .type(JsonFieldType.STRING)
                                                            .description("협업 유형 (team, personal)"),
                                                    fieldWithPath("visibility")
                                                            .type(JsonFieldType.STRING)
                                                            .description("공개 범위 (public, private)"),
                                                    fieldWithPath("title")
                                                            .type(JsonFieldType.STRING)
                                                            .description("포트폴리오 제목 (최대 200자)"),
                                                    fieldWithPath("privateMemo")
                                                            .type(JsonFieldType.STRING)
                                                            .description("나만보기 메모 (작성자에게만 노출)")
                                                            .optional(),
                                                    fieldWithPath("previewSummary")
                                                            .type(JsonFieldType.STRING)
                                                            .description("카드 노출용 한 줄 소개 (최대 100자)"),
                                                    fieldWithPath("thumbnailImageId")
                                                            .type(JsonFieldType.STRING)
                                                            .description("썸네일 imageFileMetaId (TSID 정밀도 보존을 위해 문자열로 송신, UPLOADED 전이 대상)")
                                                            .optional(),
                                                    fieldWithPath("contentImageIds")
                                                            .type(JsonFieldType.ARRAY)
                                                            .description("본문에서 참조하는 이미지 imageFileMetaId 목록 (각 원소는 TSID 정밀도 보존을 위해 문자열, UPLOADED 전이 대상)")
                                                            .optional(),
                                                    fieldWithPath("tags")
                                                            .type(JsonFieldType.ARRAY)
                                                            .description("사용자 입력 태그 목록")
                                                            .optional(),
                                                    fieldWithPath("tags[].userInputTag")
                                                            .type(JsonFieldType.STRING)
                                                            .description("사용자 입력 태그 문자열")
                                                            .optional(),
                                                    fieldWithPath("tags[].sortOrder")
                                                            .type(JsonFieldType.NUMBER)
                                                            .description("사용자가 의도한 표시 순서 (0 이상)")
                                                            .optional(),
                                                    fieldWithPath("externalLinks")
                                                            .type(JsonFieldType.ARRAY)
                                                            .description("외부 링크 목록 (label/url 쌍으로 입력)")
                                                            .optional(),
                                                    fieldWithPath("externalLinks[].label")
                                                            .type(JsonFieldType.STRING)
                                                            .description("외부 링크 표시 라벨")
                                                            .optional(),
                                                    fieldWithPath("externalLinks[].url")
                                                            .type(JsonFieldType.STRING)
                                                            .description("외부 링크 URL (http/https)")
                                                            .optional(),
                                                    fieldWithPath("content")
                                                            .type(JsonFieldType.OBJECT)
                                                            .description("포트폴리오 본문"),
                                                    fieldWithPath("content.json")
                                                            .type(JsonFieldType.OBJECT)
                                                            .description("에디터 직렬화 JSON (구조 보존용)"),
                                                    fieldWithPath("content.json.type")
                                                            .type(JsonFieldType.STRING)
                                                            .description("에디터 노드 type (예: doc)")
                                                            .optional(),
                                                    fieldWithPath("content.json.content")
                                                            .type(JsonFieldType.ARRAY)
                                                            .description("에디터 노드 content 배열")
                                                            .optional(),
                                                    fieldWithPath("content.html")
                                                            .type(JsonFieldType.STRING)
                                                            .description("렌더링된 HTML 본문 (미리보기/검색 대상)"),
                                                    fieldWithPath("linkedResumeId")
                                                            .type(JsonFieldType.STRING)
                                                            .description("연결된 이력서 ID (TSID 정밀도 보존을 위해 문자열로 송신)")
                                                            .optional(),
                                                    fieldWithPath("linkedCoverLetterId")
                                                            .type(JsonFieldType.STRING)
                                                            .description("연결된 자기소개서 ID (TSID 정밀도 보존을 위해 문자열로 송신)")
                                                            .optional()
                                            )
                                            .responseFields(
                                                    fieldWithPath("portfolioId")
                                                            .type(JsonFieldType.STRING)
                                                            .description("생성된 포트폴리오 ID (TSID 정밀도 보존을 위해 문자열로 직렬화)")
                                            )
                                            .build()
                            )
                    ));
        }
    }

    // ──────────────────────────────────────────────
    //  포트폴리오 등록: 비즈니스 로직 실패 케이스
    // ──────────────────────────────────────────────

    @Nested
    @DisplayName("포트폴리오 등록: 비즈니스 로직 실패 케이스")
    class CreatePortfolioBusinessFailureTest {

        @Test
        @DisplayName("[404 Not Found] 존재하지 않는 imageFileMetaId 참조 시 IMAGE_NOT_FOUND 에러를 반환한다.")
        void create_portfolio_image_not_found() throws Exception {
            // given
            String requestBody = createValidRequestBody();
            given(createPortfolioUseCase.execute(eq(MEMBER_ACCOUNT_ID), any()))
                    .willThrow(new FileApplicationException(
                            FileApplicationExceptionCodeCluster.DetailResponse.IMAGE_NOT_FOUND
                    ));

            // when & then
            mockMvc.perform(post("/api/portfolios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value("IMAGE_NOT_FOUND"))

                    // 문서화
                    .andDo(document("404-portfolio-create-image-not-found",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag(SwaggerDocs.Tags.Portfolio.PORTFOLIO)
                                            .responseFields(errorResponseFields())
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("[403 Forbidden] 다른 사용자의 이미지 사용 시도 시 IMAGE_OWNERSHIP_VIOLATION 에러를 반환한다.")
        void create_portfolio_image_ownership_violation() throws Exception {
            // given
            String requestBody = createValidRequestBody();
            given(createPortfolioUseCase.execute(eq(MEMBER_ACCOUNT_ID), any()))
                    .willThrow(new FileApplicationException(
                            FileApplicationExceptionCodeCluster.DetailResponse.IMAGE_OWNERSHIP_VIOLATION
                    ));

            // when & then
            mockMvc.perform(post("/api/portfolios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.errorCode").value("IMAGE_OWNERSHIP_VIOLATION"))

                    // 문서화
                    .andDo(document("403-portfolio-create-image-ownership-violation",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag(SwaggerDocs.Tags.Portfolio.PORTFOLIO)
                                            .responseFields(errorResponseFields())
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("[409 Conflict] 이미지가 PENDING 상태가 아닐 때 INVALID_UPLOAD_STATUS_TRANSITION 에러를 반환한다.")
        void create_portfolio_invalid_upload_status_transition() throws Exception {
            // given
            String requestBody = createValidRequestBody();
            given(createPortfolioUseCase.execute(eq(MEMBER_ACCOUNT_ID), any()))
                    .willThrow(new ImageFileMetaDomainException(
                            ImageFileMetaDomainExceptionCodeCluster.HiddenDetailResponse.INVALID_UPLOAD_STATUS_TRANSITION
                    ));

            // when & then
            mockMvc.perform(post("/api/portfolios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andDo(print())
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.errorCode").value("INVALID_UPLOAD_STATUS_TRANSITION"))

                    // 문서화
                    .andDo(document("409-portfolio-create-image-status-conflict",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag(SwaggerDocs.Tags.Portfolio.PORTFOLIO)
                                            .responseFields(errorResponseFields())
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("[404 Not Found] 유효하지 않은 categoryCode 사용 시 JOB_CATEGORY_CODE_NOT_FOUND 에러를 반환한다.")
        void create_portfolio_job_category_code_not_found() throws Exception {
            // given
            String requestBody = createValidRequestBody();
            given(createPortfolioUseCase.execute(eq(MEMBER_ACCOUNT_ID), any()))
                    .willThrow(new CategoryApplicationException(
                            CategoryApplicationExceptionCodeCluster.DetailResponse.JOB_CATEGORY_CODE_NOT_FOUND
                    ));

            // when & then
            mockMvc.perform(post("/api/portfolios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value("JOB_CATEGORY_CODE_NOT_FOUND"))

                    // 문서화
                    .andDo(document("404-portfolio-create-category-not-found",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag(SwaggerDocs.Tags.Portfolio.PORTFOLIO)
                                            .responseFields(errorResponseFields())
                                            .build()
                            )
                    ));
        }
    }

    // ──────────────────────────────────────────────
    //  포트폴리오 등록: 입력값 검증 실패 케이스
    // ──────────────────────────────────────────────

    @Nested
    @DisplayName("포트폴리오 등록: 입력값 검증 실패 케이스")
    class CreatePortfolioValidationFailureTest {

        @Test
        @DisplayName("[400 Bad Request] 필수 필드(title)가 누락되면 REQUEST_VALUE_INVALID 에러를 반환한다.")
        void create_portfolio_validation_missing_required_fields() throws Exception {
            // given — title을 비운 요청
            String requestBody = objectMapper.writeValueAsString(Map.ofEntries(
                    Map.entry("jobCategory", Map.of("code", "DEV_BACKEND")),
                    Map.entry("collaborationType", "team"),
                    Map.entry("visibility", "public"),
                    Map.entry("title", ""),
                    Map.entry("previewSummary", "한 줄 소개"),
                    Map.entry("content", Map.of(
                            "json", Map.of("type", "doc"),
                            "html", "<p>본문</p>"
                    ))
            ));

            // when & then
            mockMvc.perform(post("/api/portfolios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("REQUEST_VALUE_INVALID"))
                    .andExpect(jsonPath("$.fieldErrors").isArray())

                    // 문서화
                    .andDo(document("400-portfolio-create-validation",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag(SwaggerDocs.Tags.Portfolio.PORTFOLIO)
                                            .responseFields(validationErrorResponseFields())
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("[400 Bad Request] jobCategory.userInput 길이가 10자를 초과하면 REQUEST_VALUE_INVALID 에러를 반환한다.")
        void create_portfolio_validation_user_input_too_long() throws Exception {
            // given — userInput 11자
            String requestBody = objectMapper.writeValueAsString(Map.ofEntries(
                    Map.entry("jobCategory", Map.of(
                            "code", "DEV_BACKEND",
                            "userInput", "가".repeat(11)
                    )),
                    Map.entry("collaborationType", "team"),
                    Map.entry("visibility", "public"),
                    Map.entry("title", "회원 서비스 회고"),
                    Map.entry("previewSummary", "한 줄 소개"),
                    Map.entry("content", Map.of(
                            "json", Map.of("type", "doc"),
                            "html", "<p>본문</p>"
                    ))
            ));

            // when & then
            mockMvc.perform(post("/api/portfolios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("REQUEST_VALUE_INVALID"))
                    .andExpect(jsonPath("$.fieldErrors").isArray());
        }
    }

    // ──────────────────────────────────────────────
    //  포트폴리오 상세 조회: 성공 케이스
    // ──────────────────────────────────────────────

    @Nested
    @DisplayName("포트폴리오 상세 조회: 성공 케이스 (Happy Path)")
    class LoadPortfolioDetailSuccessTest {

        private static final Long PUBLIC_PORTFOLIO_ID = 7234567890123456789L;
        private static final Long PRIVATE_PORTFOLIO_ID = 1234567890123456789L;
        private static final String PUBLISHER_NICKNAME = "hirecore_user";

        private PortfolioDetailResponse buildResponse(boolean isOwner, Visibility visibility) {
            return PortfolioDetailResponse.builder()
                    .isOwner(isOwner)
                    .publisher(PUBLISHER_NICKNAME)
                    .jobCategories(List.of(
                            new PortfolioJobCategoryResponse(1001L, 1L, "DEV", "개발"),
                            new PortfolioJobCategoryResponse(1002L, 2L, "DEV_BACKEND", "백엔드")
                    ))
                    .collaborationType(CollaborationType.TEAM)
                    .visibility(visibility)
                    .viewCount(0L)
                    .interestCount(0L)
                    .title("회원 서비스 도메인 모델링 회고")
                    .content(PortfolioContentResponse.builder()
                            .json("{\"type\":\"doc\",\"content\":[]}")
                            .html("<p>본문 HTML 입니다.</p>")
                            .build())
                    .tags(List.of(
                            new PortfolioTagResponse("Spring", 0),
                            new PortfolioTagResponse("DDD", 1),
                            new PortfolioTagResponse("Hexagonal", 2)
                    ))
                    .externalLinks(List.of(
                            new PortfolioExternalLinkResponse("GitHub Repo", "https://github.com/example/repo"),
                            new PortfolioExternalLinkResponse("데모", "https://demo.example.com")
                    ))
                    .updatedAt(Instant.parse("2026-06-01T08:21:34.123456Z"))
                    .build();
        }

        @Test
        @DisplayName("[200 OK] PUBLIC 포트폴리오는 비로그인 사용자도 조회할 수 있다.")
        void load_portfolio_detail_public_anonymous() throws Exception {
            // given - 비로그인 viewer
            SecurityContextHolder.clearContext();
            given(loadPortfolioDetailUseCase.execute(eq(PUBLIC_PORTFOLIO_ID), nullable(Long.class)))
                    .willReturn(buildResponse(false, Visibility.PUBLIC));

            // when & then
            mockMvc.perform(get("/api/portfolios/{portfolioId}", PUBLIC_PORTFOLIO_ID))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isOwner").value(false))
                    .andExpect(jsonPath("$.publisher").value(PUBLISHER_NICKNAME))
                    .andExpect(jsonPath("$.jobCategories[0].categoryCode").value("DEV"))
                    .andExpect(jsonPath("$.jobCategories[0].name").value("개발"))
                    .andExpect(jsonPath("$.collaborationType").value("team"))
                    .andExpect(jsonPath("$.visibility").value("public"))
                    .andExpect(jsonPath("$.title").value("회원 서비스 도메인 모델링 회고"))
                    .andExpect(jsonPath("$.content.html").value("<p>본문 HTML 입니다.</p>"))
                    .andExpect(jsonPath("$.tags[0].userInputTag").value("Spring"))
                    .andExpect(jsonPath("$.tags[0].sortOrder").value(0))
                    .andExpect(jsonPath("$.externalLinks[0].label").value("GitHub Repo"))
                    .andExpect(jsonPath("$.externalLinks[0].url").value("https://github.com/example/repo"))

                    // 문서화
                    .andDo(document("200-portfolio-load-detail-success",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag(SwaggerDocs.Tags.Portfolio.PORTFOLIO)
                                            .summary("\"포트폴리오 상세 조회\": 단일 포트폴리오의 본문/메타데이터를 반환한다.")
                                            .description("""
                                                    상세 페이지에서 단일 포트폴리오를 조회합니다.

                                                        [공개 정책]
                                                         - PUBLIC 포트폴리오: 누구나 조회 가능 (비로그인 포함)
                                                         - PRIVATE 포트폴리오: 작성자 본인만 조회 가능
                                                         - 그 외: 403 PORTFOLIO_FORBIDDEN

                                                        [인증 방식]
                                                         - 선택. Authorization 헤더의 Bearer 토큰이 있으면 소유자 판별에 사용됩니다.
                                                         - 비로그인 호출 시 viewer ID 가 없으므로 소유자로 인정되지 않으며 isOwner=false 로 반환됩니다.

                                                        [응답 동작]
                                                         - tags 는 sortOrder 오름차순으로 정렬되어 옵니다 (삭제된 태그 제외).
                                                         - updatedAt 은 현 단계에서 항상 null 입니다 (후속 작업 예정).
                                                         - 작성자 식별자(memberAccountId 등) 와 썸네일 URL, previewSummary 는 응답에 포함되지 않습니다.

                                                        [에러 응답]
                                                         - 403 PORTFOLIO_FORBIDDEN: PRIVATE 포트폴리오 + 비소유자/비로그인
                                                         - 404 PORTFOLIO_NOT_FOUND: 존재하지 않는 portfolioId
                                                         - 404 PORTFOLIO_NICKNAME_NOT_FOUND: 작성자 닉네임 조회 실패 (데이터 정합성 이슈)
                                                    """)
                                            .pathParameters(
                                                    ResourceDocumentation.parameterWithName("portfolioId")
                                                            .type(SimpleType.STRING)
                                                            .description("조회 대상 포트폴리오 ID (TSID, JSON 문자열). path 에서도 문자열로 그대로 사용")
                                            )
                                            .responseFields(
                                                    fieldWithPath("isOwner")
                                                            .type(JsonFieldType.BOOLEAN)
                                                            .description("호출자가 작성자 본인인지 여부. 비로그인 시 항상 false. 수정/삭제 버튼 노출 등 UX 분기에 사용"),
                                                    fieldWithPath("publisher")
                                                            .type(JsonFieldType.STRING)
                                                            .description("작성자 닉네임"),
                                                    fieldWithPath("jobCategories")
                                                            .type(JsonFieldType.ARRAY)
                                                            .description("작성자가 선택한 직무 카테고리 목록"),
                                                    fieldWithPath("jobCategories[].id")
                                                            .type(JsonFieldType.STRING)
                                                            .description("직무 카테고리 ID (TSID, JSON 문자열)"),
                                                    fieldWithPath("jobCategories[].depth")
                                                            .type(JsonFieldType.NUMBER)
                                                            .description("카테고리 계층 깊이"),
                                                    fieldWithPath("jobCategories[].categoryCode")
                                                            .type(JsonFieldType.STRING)
                                                            .description("카테고리 코드"),
                                                    fieldWithPath("jobCategories[].name")
                                                            .type(JsonFieldType.STRING)
                                                            .description("카테고리 표시 이름"),
                                                    fieldWithPath("collaborationType")
                                                            .type(JsonFieldType.STRING)
                                                            .description("협업 유형 (team, personal)"),
                                                    fieldWithPath("visibility")
                                                            .type(JsonFieldType.STRING)
                                                            .description("공개 범위 (public, private)"),
                                                    fieldWithPath("viewCount")
                                                            .type(JsonFieldType.NUMBER)
                                                            .description("포트폴리오 조회수"),
                                                    fieldWithPath("interestCount")
                                                            .type(JsonFieldType.NUMBER)
                                                            .description("포트폴리오 관심등록수"),
                                                    fieldWithPath("title")
                                                            .type(JsonFieldType.STRING)
                                                            .description("포트폴리오 제목"),
                                                    fieldWithPath("content")
                                                            .type(JsonFieldType.OBJECT)
                                                            .description("포트폴리오 본문 wrapper"),
                                                    fieldWithPath("content.json")
                                                            .type(JsonFieldType.STRING)
                                                            .description("에디터 직렬화 JSON 문자열 (FE 에서 다시 JSON.parse 하여 구조 복원). 등록 시 보낸 구조 그대로"),
                                                    fieldWithPath("content.html")
                                                            .type(JsonFieldType.STRING)
                                                            .description("렌더된 HTML 본문 (표시 용도)"),
                                                    fieldWithPath("tags")
                                                            .type(JsonFieldType.ARRAY)
                                                            .description("사용자 입력 태그 목록 (sortOrder ASC, 삭제된 태그 제외)"),
                                                    fieldWithPath("tags[].userInputTag")
                                                            .type(JsonFieldType.STRING)
                                                            .description("사용자가 입력한 원본 태그 문자열"),
                                                    fieldWithPath("tags[].sortOrder")
                                                            .type(JsonFieldType.NUMBER)
                                                            .description("사용자가 의도한 표시 순서 (0부터 시작)"),
                                                    fieldWithPath("externalLinks")
                                                            .type(JsonFieldType.ARRAY)
                                                            .description("외부 링크 목록 (없으면 빈 배열)"),
                                                    fieldWithPath("externalLinks[].label")
                                                            .type(JsonFieldType.STRING)
                                                            .description("링크 표시 라벨"),
                                                    fieldWithPath("externalLinks[].url")
                                                            .type(JsonFieldType.STRING)
                                                            .description("링크 URL (http:// 또는 https://)"),
                                                    fieldWithPath("updatedAt")
                                                            .type(JsonFieldType.STRING)
                                                            .description("최종 수정 시각 (ISO-8601). 현 단계에서는 항상 null")
                                                            .optional()
                                            )
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("[200 OK] PRIVATE 포트폴리오를 소유자 본인이 조회하면 isOwner=true 로 반환한다.")
        void load_portfolio_detail_private_owner() throws Exception {
            // given - 소유자 본인 (setUpAuthentication 으로 MEMBER_ACCOUNT_ID 인증)
            given(loadPortfolioDetailUseCase.execute(eq(PRIVATE_PORTFOLIO_ID), eq(MEMBER_ACCOUNT_ID)))
                    .willReturn(buildResponse(true, Visibility.PRIVATE));

            // when & then
            mockMvc.perform(get("/api/portfolios/{portfolioId}", PRIVATE_PORTFOLIO_ID))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isOwner").value(true))
                    .andExpect(jsonPath("$.visibility").value("private"));
        }
    }

    // ──────────────────────────────────────────────
    //  포트폴리오 상세 조회: 비즈니스 로직 실패 케이스
    // ──────────────────────────────────────────────

    @Nested
    @DisplayName("포트폴리오 상세 조회: 비즈니스 로직 실패 케이스")
    class LoadPortfolioDetailBusinessFailureTest {

        private static final Long PRIVATE_PORTFOLIO_ID = 1234567890123456789L;
        private static final Long NONEXISTENT_PORTFOLIO_ID = 9000000000000000000L;

        @Test
        @DisplayName("[403 Forbidden] PRIVATE 포트폴리오를 비소유자가 조회 시도하면 PORTFOLIO_FORBIDDEN 에러를 반환한다.")
        void load_portfolio_detail_forbidden() throws Exception {
            // given
            given(loadPortfolioDetailUseCase.execute(eq(PRIVATE_PORTFOLIO_ID), nullable(Long.class)))
                    .willThrow(new PortfolioApplicationException(
                            PortfolioApplicationExceptionCodeCluster.DetailResponse.PORTFOLIO_FORBIDDEN
                    ));

            // when & then
            mockMvc.perform(get("/api/portfolios/{portfolioId}", PRIVATE_PORTFOLIO_ID))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.errorCode").value("PORTFOLIO_FORBIDDEN"))

                    // 문서화
                    .andDo(document("403-portfolio-load-detail-forbidden",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag(SwaggerDocs.Tags.Portfolio.PORTFOLIO)
                                            .responseFields(errorResponseFields())
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("[404 Not Found] 존재하지 않는 portfolioId 조회 시 PORTFOLIO_NOT_FOUND 에러를 반환한다.")
        void load_portfolio_detail_not_found() throws Exception {
            // given
            given(loadPortfolioDetailUseCase.execute(eq(NONEXISTENT_PORTFOLIO_ID), nullable(Long.class)))
                    .willThrow(new PortfolioApplicationException(
                            PortfolioApplicationExceptionCodeCluster.DetailResponse.PORTFOLIO_NOT_FOUND
                    ));

            // when & then
            mockMvc.perform(get("/api/portfolios/{portfolioId}", NONEXISTENT_PORTFOLIO_ID))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value("PORTFOLIO_NOT_FOUND"))

                    // 문서화
                    .andDo(document("404-portfolio-load-detail-not-found",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag(SwaggerDocs.Tags.Portfolio.PORTFOLIO)
                                            .responseFields(errorResponseFields())
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("[404 Not Found] 작성자 닉네임 조회 실패 시 PORTFOLIO_NICKNAME_NOT_FOUND 에러를 반환한다.")
        void load_portfolio_detail_nickname_not_found() throws Exception {
            // given - 데이터 정합성 이슈로 닉네임이 없는 케이스
            given(loadPortfolioDetailUseCase.execute(eq(PRIVATE_PORTFOLIO_ID), nullable(Long.class)))
                    .willThrow(new PortfolioApplicationException(
                            PortfolioApplicationExceptionCodeCluster.DetailResponse.PORTFOLIO_NICKNAME_NOT_FOUND
                    ));

            // when & then
            mockMvc.perform(get("/api/portfolios/{portfolioId}", PRIVATE_PORTFOLIO_ID))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value("PORTFOLIO_NICKNAME_NOT_FOUND"));
        }
    }

    // ──────────────────────────────────────────────
    //  포트폴리오 편집용 조회: 성공 케이스
    // ──────────────────────────────────────────────

    @Nested
    @DisplayName("포트폴리오 편집용 조회: 성공 케이스 (Happy Path)")
    class LoadPortfolioEditSuccessTest {

        private static final Long OWNED_PORTFOLIO_ID = 5234567890123456789L;

        private PortfolioEditResponse buildResponse() {
            return new PortfolioEditResponse(
                    "회고 작성 시 참고용 메모입니다.",
                    "회원 서비스를 도메인 모델링한 회고를 정리한 글입니다.",
                    "https://cdn.example.com/portfolio/thumbnail/2026/06/9876543210987654321.webp",
                    List.of(
                            new PortfolioJobCategoryResponse(1001L, 1L, "DEV", "개발"),
                            new PortfolioJobCategoryResponse(1002L, 2L, "DEV_BACKEND", "백엔드")
                    ),
                    CollaborationType.TEAM,
                    Visibility.PUBLIC,
                    "회원 서비스 도메인 모델링 회고",
                    List.of(
                            new PortfolioTagResponse("Spring", 0),
                            new PortfolioTagResponse("DDD", 1)
                    ),
                    List.of(
                            new PortfolioExternalLinkResponse("GitHub Repo", "https://github.com/example/repo")
                    ),
                    PortfolioContentResponse.builder()
                            .json("{\"type\":\"doc\",\"content\":[]}")
                            .html("<p>본문 HTML 입니다.</p>")
                            .build()
            );
        }

        @Test
        @DisplayName("[200 OK] 작성자 본인이 호출하면 편집용 응답을 반환한다.")
        void load_portfolio_edit_owner() throws Exception {
            // given - 소유자 본인 인증 (setUpAuthentication 으로 MEMBER_ACCOUNT_ID 인증)
            given(loadPortfolioEditUseCase.execute(eq(OWNED_PORTFOLIO_ID), eq(MEMBER_ACCOUNT_ID)))
                    .willReturn(buildResponse());

            // when & then
            mockMvc.perform(get("/api/portfolios/{portfolioId}/edit", OWNED_PORTFOLIO_ID))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.privateMemo").value("회고 작성 시 참고용 메모입니다."))
                    .andExpect(jsonPath("$.previewSummary").value("회원 서비스를 도메인 모델링한 회고를 정리한 글입니다."))
                    .andExpect(jsonPath("$.thumbnailImageUrl").value("https://cdn.example.com/portfolio/thumbnail/2026/06/9876543210987654321.webp"))
                    .andExpect(jsonPath("$.jobCategories[0].categoryCode").value("DEV"))
                    .andExpect(jsonPath("$.collaborationType").value("team"))
                    .andExpect(jsonPath("$.visibility").value("public"))
                    .andExpect(jsonPath("$.title").value("회원 서비스 도메인 모델링 회고"))
                    .andExpect(jsonPath("$.content.html").value("<p>본문 HTML 입니다.</p>"))
                    .andExpect(jsonPath("$.tags[0].userInputTag").value("Spring"))
                    .andExpect(jsonPath("$.externalLinks[0].url").value("https://github.com/example/repo"))

                    // 문서화
                    .andDo(document("200-portfolio-load-edit-success",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag(SwaggerDocs.Tags.Portfolio.PORTFOLIO)
                                            .summary("\"포트폴리오 편집용 조회\": 작성자 본인의 편집 폼 초기화용 데이터를 반환한다.")
                                            .description("""
                                                    편집 화면에서 작성자 본인의 포트폴리오를 초기 로드할 때 사용합니다.

                                                        [접근 정책]
                                                         - 작성자 본인만 호출 가능
                                                         - 비로그인 또는 비소유자: 403 PORTFOLIO_FORBIDDEN
                                                         - 존재하지 않는 포트폴리오: 404 PORTFOLIO_NOT_FOUND

                                                        [인증 방식]
                                                         - 필수. Authorization 헤더의 Bearer 토큰으로 본인 식별 후 작성자 일치 확인.

                                                        [상세 조회 API 와의 차이]
                                                         - 본 API: 편집 폼 초기화 전용. privateMemo, previewSummary, thumbnailImagePath 포함.
                                                         - 상세 조회 API: 표시 전용. isOwner, publisher, viewCount, interestCount, updatedAt 포함.

                                                        [응답 동작]
                                                         - thumbnailImageUrl 은 서버가 환경별 CloudFront/CDN base URL 과 object key 를 조합해 만든 전체 URL. 썸네일 미등록 시 null.
                                                         - tags 는 sortOrder 오름차순으로 정렬.
                                                         - 조회수 카운트 증가 없음 (자가 조회).

                                                        [에러 응답]
                                                         - 403 PORTFOLIO_FORBIDDEN: 비로그인 또는 비소유자
                                                         - 404 PORTFOLIO_NOT_FOUND: 존재하지 않는 portfolioId
                                                    """)
                                            .pathParameters(
                                                    ResourceDocumentation.parameterWithName("portfolioId")
                                                            .type(SimpleType.STRING)
                                                            .description("편집 대상 포트폴리오 ID (TSID, JSON 문자열)")
                                            )
                                            .responseFields(
                                                    fieldWithPath("privateMemo")
                                                            .type(JsonFieldType.STRING)
                                                            .description("작성자 비공개 메모. 소유자에게만 노출. 미작성 시 null")
                                                            .optional(),
                                                    fieldWithPath("previewSummary")
                                                            .type(JsonFieldType.STRING)
                                                            .description("미리보기 요약 텍스트"),
                                                    fieldWithPath("thumbnailImageUrl")
                                                            .type(JsonFieldType.STRING)
                                                            .description("썸네일 이미지의 전체 URL (환경별 CloudFront/CDN base URL + object key 가 서버에서 조합됨). `null` 인 경우 사용자가 포트폴리오 등록 시 썸네일을 등록하지 않은 상태로 해석. 편집 UI 는 이 상태를 신규 업로드(드롭존 등) 노출 근거로 사용 가능.")
                                                            .optional(),
                                                    fieldWithPath("jobCategories")
                                                            .type(JsonFieldType.ARRAY)
                                                            .description("작성자가 선택한 직무 카테고리 계층 (루트 → 리프)"),
                                                    fieldWithPath("jobCategories[].id")
                                                            .type(JsonFieldType.STRING)
                                                            .description("직무 카테고리 ID (TSID, JSON 문자열)"),
                                                    fieldWithPath("jobCategories[].depth")
                                                            .type(JsonFieldType.NUMBER)
                                                            .description("카테고리 계층 깊이"),
                                                    fieldWithPath("jobCategories[].categoryCode")
                                                            .type(JsonFieldType.STRING)
                                                            .description("카테고리 코드"),
                                                    fieldWithPath("jobCategories[].name")
                                                            .type(JsonFieldType.STRING)
                                                            .description("카테고리 표시 이름"),
                                                    fieldWithPath("collaborationType")
                                                            .type(JsonFieldType.STRING)
                                                            .description("협업 유형 (team, personal)"),
                                                    fieldWithPath("visibility")
                                                            .type(JsonFieldType.STRING)
                                                            .description("공개 범위 (public, private)"),
                                                    fieldWithPath("title")
                                                            .type(JsonFieldType.STRING)
                                                            .description("포트폴리오 제목"),
                                                    fieldWithPath("content")
                                                            .type(JsonFieldType.OBJECT)
                                                            .description("포트폴리오 본문 wrapper"),
                                                    fieldWithPath("content.json")
                                                            .type(JsonFieldType.STRING)
                                                            .description("에디터 직렬화 JSON 문자열 (편집 폼 초기화 시 에디터에 그대로 주입)"),
                                                    fieldWithPath("content.html")
                                                            .type(JsonFieldType.STRING)
                                                            .description("렌더된 HTML 본문 (미리보기 용도)"),
                                                    fieldWithPath("tags")
                                                            .type(JsonFieldType.ARRAY)
                                                            .description("사용자 입력 태그 목록 (sortOrder ASC)"),
                                                    fieldWithPath("tags[].userInputTag")
                                                            .type(JsonFieldType.STRING)
                                                            .description("사용자가 입력한 원본 태그 문자열"),
                                                    fieldWithPath("tags[].sortOrder")
                                                            .type(JsonFieldType.NUMBER)
                                                            .description("사용자가 의도한 표시 순서 (0부터 시작)"),
                                                    fieldWithPath("externalLinks")
                                                            .type(JsonFieldType.ARRAY)
                                                            .description("외부 링크 목록 (없으면 빈 배열)"),
                                                    fieldWithPath("externalLinks[].label")
                                                            .type(JsonFieldType.STRING)
                                                            .description("링크 표시 라벨"),
                                                    fieldWithPath("externalLinks[].url")
                                                            .type(JsonFieldType.STRING)
                                                            .description("링크 URL (http:// 또는 https://)")
                                            )
                                            .build()
                            )
                    ));
        }
    }

    // ──────────────────────────────────────────────
    //  포트폴리오 편집용 조회: 비즈니스 로직 실패 케이스
    // ──────────────────────────────────────────────

    @Nested
    @DisplayName("포트폴리오 편집용 조회: 비즈니스 로직 실패 케이스")
    class LoadPortfolioEditBusinessFailureTest {

        private static final Long OTHERS_PORTFOLIO_ID = 6234567890123456789L;
        private static final Long NONEXISTENT_PORTFOLIO_ID = 9000000000000000001L;

        @Test
        @DisplayName("[403 Forbidden] 비소유자가 편집 조회를 시도하면 PORTFOLIO_FORBIDDEN 에러를 반환한다.")
        void load_portfolio_edit_forbidden_non_owner() throws Exception {
            // given - 다른 사용자의 포트폴리오 호출
            given(loadPortfolioEditUseCase.execute(eq(OTHERS_PORTFOLIO_ID), eq(MEMBER_ACCOUNT_ID)))
                    .willThrow(new PortfolioApplicationException(
                            PortfolioApplicationExceptionCodeCluster.DetailResponse.PORTFOLIO_FORBIDDEN
                    ));

            // when & then
            mockMvc.perform(get("/api/portfolios/{portfolioId}/edit", OTHERS_PORTFOLIO_ID))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.errorCode").value("PORTFOLIO_FORBIDDEN"))

                    // 문서화
                    .andDo(document("403-portfolio-load-edit-forbidden",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag(SwaggerDocs.Tags.Portfolio.PORTFOLIO)
                                            .responseFields(errorResponseFields())
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("[404 Not Found] 존재하지 않는 portfolioId 편집 조회 시 PORTFOLIO_NOT_FOUND 에러를 반환한다.")
        void load_portfolio_edit_not_found() throws Exception {
            // given
            given(loadPortfolioEditUseCase.execute(eq(NONEXISTENT_PORTFOLIO_ID), eq(MEMBER_ACCOUNT_ID)))
                    .willThrow(new PortfolioApplicationException(
                            PortfolioApplicationExceptionCodeCluster.DetailResponse.PORTFOLIO_NOT_FOUND
                    ));

            // when & then
            mockMvc.perform(get("/api/portfolios/{portfolioId}/edit", NONEXISTENT_PORTFOLIO_ID))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value("PORTFOLIO_NOT_FOUND"))

                    // 문서화
                    .andDo(document("404-portfolio-load-edit-not-found",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag(SwaggerDocs.Tags.Portfolio.PORTFOLIO)
                                            .responseFields(errorResponseFields())
                                            .build()
                            )
                    ));
        }
    }

    // ──────────────────────────────────────────────
    //  포트폴리오 수정: 성공 케이스
    // ──────────────────────────────────────────────

    @Nested
    @DisplayName("포트폴리오 수정: 성공 케이스 (Happy Path)")
    class UpdatePortfolioSuccessTest {

        private static final Long TARGET_PORTFOLIO_ID = 5234567890123456789L;

        @Test
        @DisplayName("[200 OK] 작성자 본인이 PUT 요청하면 portfolioId 를 반환한다.")
        void update_portfolio_success() throws Exception {
            // given - 소유자 본인 인증 (setUpAuthentication)
            String requestBody = createValidRequestBody();
            given(updatePortfolioUseCase.execute(eq(TARGET_PORTFOLIO_ID), eq(MEMBER_ACCOUNT_ID), any()))
                    .willReturn(TARGET_PORTFOLIO_ID);

            // when & then
            mockMvc.perform(put("/api/portfolios/{portfolioId}", TARGET_PORTFOLIO_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.portfolioId").value(String.valueOf(TARGET_PORTFOLIO_ID)))

                    // 문서화
                    .andDo(document("200-portfolio-update-success",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag(SwaggerDocs.Tags.Portfolio.PORTFOLIO)
                                            .summary("\"포트폴리오 수정\": 작성자 본인의 포트폴리오를 전체 교체 갱신한다.")
                                            .description("""
                                                    작성자가 편집 화면에서 변경한 포트폴리오를 저장합니다 (PUT 전체 교체 시맨틱).

                                                        [접근 정책]
                                                         - 작성자 본인만 호출 가능
                                                         - 비로그인 또는 비소유자: 403 PORTFOLIO_FORBIDDEN
                                                         - 존재하지 않는 포트폴리오: 404 PORTFOLIO_NOT_FOUND

                                                        [처리 흐름]
                                                         1. 신규 이미지(thumbnailImageId, contentImageIds)에 대해 markUploaded 호출 (PENDING → UPLOADED 전이)
                                                         2. jobCategory.code 를 직무 카테고리 ID 로 해석
                                                         3. Portfolio.modify(...) 로 도메인 invariant 재검증 + 자식 컬렉션 전체 교체
                                                         4. UpdatePortfolioPort.update(...) 로 영속화 (cascade + orphanRemoval)

                                                        [범위 외]
                                                         - 이전 썸네일/본문 이미지의 storage cleanup 은 별도 이슈에서 처리 예정

                                                        [응답]
                                                         - 200 OK + body { portfolioId } (등록 응답과 일관)

                                                        [에러 응답]
                                                         - 400 REQUEST_VALUE_INVALID: 요청 본문 필수 필드 누락
                                                         - 403 PORTFOLIO_FORBIDDEN: 비소유자/비로그인
                                                         - 404 PORTFOLIO_NOT_FOUND: 존재하지 않는 portfolioId
                                                    """)
                                            .pathParameters(
                                                    ResourceDocumentation.parameterWithName("portfolioId")
                                                            .type(SimpleType.STRING)
                                                            .description("수정 대상 포트폴리오 ID (TSID, JSON 문자열)")
                                            )
                                            .requestFields(
                                                    fieldWithPath("jobCategory")
                                                            .type(JsonFieldType.OBJECT)
                                                            .description("직무 카테고리 선택 정보"),
                                                    fieldWithPath("jobCategory.code")
                                                            .type(JsonFieldType.STRING)
                                                            .description("직무 카테고리 코드 (예: DEV_BACKEND)"),
                                                    fieldWithPath("jobCategory.userInput")
                                                            .type(JsonFieldType.STRING)
                                                            .description("사용자 입력 카테고리 라벨")
                                                            .optional(),
                                                    fieldWithPath("collaborationType")
                                                            .type(JsonFieldType.STRING)
                                                            .description("협업 유형 (team, personal)"),
                                                    fieldWithPath("visibility")
                                                            .type(JsonFieldType.STRING)
                                                            .description("공개 범위 (public, private)"),
                                                    fieldWithPath("title")
                                                            .type(JsonFieldType.STRING)
                                                            .description("포트폴리오 제목"),
                                                    fieldWithPath("privateMemo")
                                                            .type(JsonFieldType.STRING)
                                                            .description("나만보기 메모")
                                                            .optional(),
                                                    fieldWithPath("previewSummary")
                                                            .type(JsonFieldType.STRING)
                                                            .description("한 줄 소개 (최대 100자)"),
                                                    fieldWithPath("thumbnailImageId")
                                                            .type(JsonFieldType.STRING)
                                                            .description("썸네일 imageFileMetaId (TSID 문자열). null 이면 썸네일 제거 의도")
                                                            .optional(),
                                                    fieldWithPath("contentImageIds")
                                                            .type(JsonFieldType.ARRAY)
                                                            .description("본문 참조 imageFileMetaId 목록")
                                                            .optional(),
                                                    fieldWithPath("tags")
                                                            .type(JsonFieldType.ARRAY)
                                                            .description("사용자 입력 태그 목록 (전체 교체)")
                                                            .optional(),
                                                    fieldWithPath("tags[].userInputTag")
                                                            .type(JsonFieldType.STRING)
                                                            .description("태그 문자열")
                                                            .optional(),
                                                    fieldWithPath("tags[].sortOrder")
                                                            .type(JsonFieldType.NUMBER)
                                                            .description("표시 순서 (0 이상)")
                                                            .optional(),
                                                    fieldWithPath("externalLinks")
                                                            .type(JsonFieldType.ARRAY)
                                                            .description("외부 링크 목록 (전체 교체)")
                                                            .optional(),
                                                    fieldWithPath("externalLinks[].label")
                                                            .type(JsonFieldType.STRING)
                                                            .description("링크 라벨")
                                                            .optional(),
                                                    fieldWithPath("externalLinks[].url")
                                                            .type(JsonFieldType.STRING)
                                                            .description("링크 URL (http/https)")
                                                            .optional(),
                                                    fieldWithPath("content")
                                                            .type(JsonFieldType.OBJECT)
                                                            .description("포트폴리오 본문 wrapper"),
                                                    fieldWithPath("content.json")
                                                            .type(JsonFieldType.OBJECT)
                                                            .description("에디터 직렬화 JSON 구조"),
                                                    fieldWithPath("content.json.type")
                                                            .type(JsonFieldType.STRING)
                                                            .description("에디터 노드 type")
                                                            .optional(),
                                                    fieldWithPath("content.json.content")
                                                            .type(JsonFieldType.ARRAY)
                                                            .description("에디터 노드 content 배열")
                                                            .optional(),
                                                    fieldWithPath("content.html")
                                                            .type(JsonFieldType.STRING)
                                                            .description("렌더링된 HTML 본문"),
                                                    fieldWithPath("linkedResumeId")
                                                            .type(JsonFieldType.STRING)
                                                            .description("연결된 이력서 ID (TSID 문자열)")
                                                            .optional(),
                                                    fieldWithPath("linkedCoverLetterId")
                                                            .type(JsonFieldType.STRING)
                                                            .description("연결된 자기소개서 ID (TSID 문자열)")
                                                            .optional()
                                            )
                                            .responseFields(
                                                    fieldWithPath("portfolioId")
                                                            .type(JsonFieldType.STRING)
                                                            .description("수정된 포트폴리오 ID (path variable 과 동일, TSID 문자열)")
                                            )
                                            .build()
                            )
                    ));
        }
    }

    // ──────────────────────────────────────────────
    //  포트폴리오 수정: 비즈니스 로직 실패 케이스
    // ──────────────────────────────────────────────

    @Nested
    @DisplayName("포트폴리오 수정: 비즈니스 로직 실패 케이스")
    class UpdatePortfolioBusinessFailureTest {

        private static final Long OTHERS_PORTFOLIO_ID = 6234567890123456789L;
        private static final Long NONEXISTENT_PORTFOLIO_ID = 9000000000000000001L;

        @Test
        @DisplayName("[403 Forbidden] 비소유자가 PUT 요청하면 PORTFOLIO_FORBIDDEN 에러를 반환한다.")
        void update_portfolio_forbidden_non_owner() throws Exception {
            // given
            String requestBody = createValidRequestBody();
            given(updatePortfolioUseCase.execute(eq(OTHERS_PORTFOLIO_ID), eq(MEMBER_ACCOUNT_ID), any()))
                    .willThrow(new PortfolioApplicationException(
                            PortfolioApplicationExceptionCodeCluster.DetailResponse.PORTFOLIO_FORBIDDEN
                    ));

            // when & then
            mockMvc.perform(put("/api/portfolios/{portfolioId}", OTHERS_PORTFOLIO_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.errorCode").value("PORTFOLIO_FORBIDDEN"))

                    // 문서화
                    .andDo(document("403-portfolio-update-forbidden",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag(SwaggerDocs.Tags.Portfolio.PORTFOLIO)
                                            .responseFields(errorResponseFields())
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("[404 Not Found] 존재하지 않는 portfolioId PUT 요청 시 PORTFOLIO_NOT_FOUND 에러를 반환한다.")
        void update_portfolio_not_found() throws Exception {
            // given
            String requestBody = createValidRequestBody();
            given(updatePortfolioUseCase.execute(eq(NONEXISTENT_PORTFOLIO_ID), eq(MEMBER_ACCOUNT_ID), any()))
                    .willThrow(new PortfolioApplicationException(
                            PortfolioApplicationExceptionCodeCluster.DetailResponse.PORTFOLIO_NOT_FOUND
                    ));

            // when & then
            mockMvc.perform(put("/api/portfolios/{portfolioId}", NONEXISTENT_PORTFOLIO_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value("PORTFOLIO_NOT_FOUND"))

                    // 문서화
                    .andDo(document("404-portfolio-update-not-found",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag(SwaggerDocs.Tags.Portfolio.PORTFOLIO)
                                            .responseFields(errorResponseFields())
                                            .build()
                            )
                    ));
        }
    }

    // ──────────────────────────────────────────────
    //  공통 응답 필드 정의
    // ──────────────────────────────────────────────

    private static org.springframework.restdocs.payload.FieldDescriptor[] errorResponseFields() {
        return new org.springframework.restdocs.payload.FieldDescriptor[]{
                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("에러 발생 시각 (ISO-8601)"),
                fieldWithPath("trackingId").type(JsonFieldType.STRING).description("에러 추적용 고유 ID"),
                fieldWithPath("errorCode").type(JsonFieldType.STRING).description("에러 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("사용자 안내 메시지"),
                fieldWithPath("path").type(JsonFieldType.STRING).description("요청 URI 경로"),
                fieldWithPath("attributes").type(JsonFieldType.OBJECT).description("부가 데이터").optional()
        };
    }

    private static org.springframework.restdocs.payload.FieldDescriptor[] validationErrorResponseFields() {
        return new org.springframework.restdocs.payload.FieldDescriptor[]{
                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("에러 발생 시각 (ISO-8601)"),
                fieldWithPath("trackingId").type(JsonFieldType.STRING).description("에러 추적용 고유 ID"),
                fieldWithPath("errorCode").type(JsonFieldType.STRING).description("에러 코드 (REQUEST_VALUE_INVALID)"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("사용자 안내 메시지"),
                fieldWithPath("path").type(JsonFieldType.STRING).description("요청 URI 경로"),
                fieldWithPath("fieldErrors").type(JsonFieldType.ARRAY).description("필드별 검증 실패 상세 목록"),
                fieldWithPath("fieldErrors[].field").type(JsonFieldType.STRING).description("검증에 실패한 필드명"),
                fieldWithPath("fieldErrors[].reason").type(JsonFieldType.STRING).description("검증 실패 사유"),
                fieldWithPath("attributes").type(JsonFieldType.OBJECT).description("부가 데이터").optional()
        };
    }
}

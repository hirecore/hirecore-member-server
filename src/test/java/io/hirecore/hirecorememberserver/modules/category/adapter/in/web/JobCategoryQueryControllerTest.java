package io.hirecore.hirecorememberserver.modules.category.adapter.in.web;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import io.hirecore.hirecorememberserver.common.config.StrictJsonConfig;
import io.hirecore.hirecorememberserver.common.security.WebMvcSecuritySupport;
import io.hirecore.hirecorememberserver.common.web.SwaggerDocs;
import io.hirecore.hirecorememberserver.modules.category.adapter.in.web.dto.JobCategoryApi;
import io.hirecore.hirecorememberserver.modules.category.adapter.in.web.mapper.JobCategoryWebMapper;
import io.hirecore.hirecorememberserver.modules.category.application.port.in.LoadJobCategoryTreeUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link JobCategoryQueryController} 웹 계층 테스트.
 *
 * <p>{@code /api/categories} 는 인증이 필요 없는 공개 엔드포인트이므로
 * 보안 필터를 비활성화하고 입력 검증/응답 매핑/문서화에 집중합니다.</p>
 */
@DisplayName("JobCategoryQueryController 웹 계층 테스트")
@WebMvcTest(controllers = JobCategoryQueryController.class)
@Import({WebMvcSecuritySupport.class, StrictJsonConfig.class})
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
class JobCategoryQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoadJobCategoryTreeUseCase loadJobCategoryTreeUseCase;

    @MockitoBean
    private JobCategoryWebMapper jobCategoryWebMapper;

    @Nested
    @DisplayName("성공 케이스 (Happy Path)")
    class SuccessTest {

        @Test
        @DisplayName("[200 OK] max-depth=2 로 요청하면 depth 2 이하 활성 카테고리 트리를 반환한다")
        void should_return_categories_within_max_depth() throws Exception {
            // given
            List<LoadJobCategoryTreeUseCase.Response> applicationResponse = List.of(
                    new LoadJobCategoryTreeUseCase.Response(1L, 1, 1, null, "개발", "DEV", false),
                    new LoadJobCategoryTreeUseCase.Response(2L, 1, 2, null, "디자인", "DESIGN", false),
                    new LoadJobCategoryTreeUseCase.Response(11L, 2, 1, 1L, "백엔드", "DEV_BACKEND", true),
                    new LoadJobCategoryTreeUseCase.Response(12L, 2, 2, 1L, "프론트엔드", "DEV_FRONTEND", true)
            );
            List<JobCategoryApi.Response> apiResponses = List.of(
                    new JobCategoryApi.Response(1L, 1, 1, null, "개발", "DEV", false),
                    new JobCategoryApi.Response(2L, 1, 2, null, "디자인", "DESIGN", false),
                    new JobCategoryApi.Response(11L, 2, 1, 1L, "백엔드", "DEV_BACKEND", true),
                    new JobCategoryApi.Response(12L, 2, 2, 1L, "프론트엔드", "DEV_FRONTEND", true)
            );
            given(loadJobCategoryTreeUseCase.execute(2)).willReturn(applicationResponse);
            given(jobCategoryWebMapper.toApiResponses(applicationResponse)).willReturn(apiResponses);

            // when & then
            mockMvc.perform(get("/api/categories")
                            .param("max-depth", "2"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.categories").isArray())
                    .andExpect(jsonPath("$.categories.length()").value(4))
                    .andExpect(jsonPath("$.categories[0].id").value("1"))
                    .andExpect(jsonPath("$.categories[0].depth").value(1))
                    .andExpect(jsonPath("$.categories[0].sortOrder").value(1))
                    .andExpect(jsonPath("$.categories[0].parentId").doesNotExist())
                    .andExpect(jsonPath("$.categories[0].categoryName").value("개발"))
                    .andExpect(jsonPath("$.categories[0].categoryCode").value("DEV"))
                    .andExpect(jsonPath("$.categories[2].parentId").value("1"))
                    .andExpect(jsonPath("$.categories[2].allowsCustomInput").value(true))

                    // 문서화
                    .andDo(document("200-job-categories-success",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag(SwaggerDocs.Tags.Category.JOB_CATEGORY)
                                            .summary("\"직무 카테고리 트리 조회\": 지정한 깊이 이하의 활성 카테고리 노드 목록을 반환한다.")
                                            .description("""
                                                    직무 카테고리 트리를 평탄화(flatten)된 노드 리스트로 반환합니다.
                                                    클라이언트는 `parentId` 와 `depth` 를 활용해 트리를 재구성할 수 있습니다.

                                                        [인증]
                                                         - 공개 엔드포인트 (인증 불필요)

                                                        [요청 파라미터]
                                                         - max-depth (필수, 1~3): 조회할 최대 깊이
                                                           · 홈 화면(검색 필터): max-depth=2
                                                           · 작성 화면(이력서/포트폴리오 폼): max-depth=3

                                                        [필터링 규칙]
                                                         - `is_active = true` 인 카테고리만 반환합니다 (비활성 항목 제외).
                                                         - `depth <= max-depth` 조건을 만족하는 노드만 포함합니다.

                                                        [정렬 규칙]
                                                         - `depth` 오름차순 → `parentId` 오름차순 → `sortOrder` 오름차순
                                                         - 부모가 자식보다 항상 먼저 등장하므로 클라이언트 트리 빌드가 단순해집니다.

                                                        [에러 응답]
                                                         - 400 Bad Request: max-depth 누락, 1 미만, 3 초과인 경우
                                                    """)
                                            .queryParameters(
                                                    parameterWithName("max-depth")
                                                            .description("조회할 최대 깊이 (1~3, 필수)")
                                            )
                                            .responseFields(
                                                    fieldWithPath("categories")
                                                            .type(JsonFieldType.ARRAY)
                                                            .description("조건을 만족하는 카테고리 노드 목록 (정렬됨)"),
                                                    fieldWithPath("categories[].id")
                                                            .type(JsonFieldType.STRING)
                                                            .description("카테고리 고유 ID (TSID 정밀도 보존을 위해 문자열로 직렬화)"),
                                                    fieldWithPath("categories[].depth")
                                                            .type(JsonFieldType.NUMBER)
                                                            .description("카테고리 깊이 (1=루트)"),
                                                    fieldWithPath("categories[].sortOrder")
                                                            .type(JsonFieldType.NUMBER)
                                                            .description("동일 부모 내 표시 순서"),
                                                    fieldWithPath("categories[].parentId")
                                                            .type(JsonFieldType.STRING)
                                                            .description("상위 카테고리 ID (루트 카테고리는 응답에서 생략됨, TSID 정밀도 보존을 위해 문자열로 직렬화)")
                                                            .optional(),
                                                    fieldWithPath("categories[].categoryName")
                                                            .type(JsonFieldType.STRING)
                                                            .description("카테고리 표시 이름"),
                                                    fieldWithPath("categories[].categoryCode")
                                                            .type(JsonFieldType.STRING)
                                                            .description("카테고리 코드 (시스템 식별용)"),
                                                    fieldWithPath("categories[].allowsCustomInput")
                                                            .type(JsonFieldType.BOOLEAN)
                                                            .description("사용자 정의 입력 허용 여부 (true 인 노드는 자유 입력 입력 필드를 노출)")
                                            )
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("[200 OK] 조건에 해당하는 카테고리가 없으면 빈 리스트를 반환한다")
        void should_return_empty_list_when_no_categories() throws Exception {
            // given
            given(loadJobCategoryTreeUseCase.execute(1)).willReturn(List.of());
            given(jobCategoryWebMapper.toApiResponses(List.of())).willReturn(List.of());

            // when & then
            mockMvc.perform(get("/api/categories").param("max-depth", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.categories").isArray())
                    .andExpect(jsonPath("$.categories.length()").value(0));
        }
    }

    @Nested
    @DisplayName("입력값 검증 실패 케이스")
    class ValidationFailureTest {

        @Test
        @DisplayName("[400 Bad Request] max-depth 파라미터를 누락하면 REQUEST_VALUE_INVALID 에러를 반환한다")
        void should_return_400_when_max_depth_missing() throws Exception {
            mockMvc.perform(get("/api/categories"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("REQUEST_VALUE_INVALID"))
                    .andExpect(jsonPath("$.fieldErrors[0].field").value("max-depth"));
        }

        @Test
        @DisplayName("[400 Bad Request] max-depth 가 1 미만이면 REQUEST_CONSTRAINT_VIOLATED 에러를 반환한다")
        void should_return_400_when_max_depth_below_min() throws Exception {
            mockMvc.perform(get("/api/categories").param("max-depth", "0"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("REQUEST_CONSTRAINT_VIOLATED"));
        }

        @Test
        @DisplayName("[400 Bad Request] max-depth 가 3 초과면 REQUEST_CONSTRAINT_VIOLATED 에러를 반환한다")
        void should_return_400_when_max_depth_above_max() throws Exception {
            mockMvc.perform(get("/api/categories").param("max-depth", "4"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("REQUEST_CONSTRAINT_VIOLATED"));
        }

        @Test
        @DisplayName("[400 Bad Request] max-depth 가 정수가 아니면 REQUEST_VALUE_INVALID 에러를 반환한다")
        void should_return_400_when_max_depth_not_integer() throws Exception {
            mockMvc.perform(get("/api/categories").param("max-depth", "abc"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("REQUEST_VALUE_INVALID"))
                    .andExpect(jsonPath("$.fieldErrors[0].field").value("max-depth"));
        }
    }
}

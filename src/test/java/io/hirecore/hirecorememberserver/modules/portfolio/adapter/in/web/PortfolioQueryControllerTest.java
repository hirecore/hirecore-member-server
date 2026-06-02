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
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadPortfolioDetailUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadPortfolioEditUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response.PortfolioContentImageResponse;
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

@DisplayName("PortfolioQueryController 웹 계층 테스트")
@WebMvcTest(controllers = PortfolioQueryController.class)
@Import({
        WebMvcSecuritySupport.class,
        StrictJsonConfig.class,
        PortfolioWebMapperImpl.class,
        SharedDomainVoWebMapperImpl.class
})
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
class PortfolioQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LoadPortfolioDetailUseCase loadPortfolioDetailUseCase;

    @MockitoBean
    private LoadPortfolioEditUseCase loadPortfolioEditUseCase;

    private static final Long MEMBER_ACCOUNT_ID = 1L;

    @BeforeEach
    void setUpAuthentication() {
        AuthPrincipal principal = new AuthPrincipal(MEMBER_ACCOUNT_ID, "user@example.com", "USER", 0);
        var auth = new UsernamePasswordAuthenticationToken(principal, "token", principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
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
                    7876543210987654321L,
                    "https://cdn.example.com/portfolio/thumbnail/2026/06/7876543210987654321.webp",
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
                    List.of(
                            new PortfolioContentImageResponse(
                                    1111111111111111111L,
                                    "https://cdn.example.com/portfolio/content/2026/06/1111111111111111111.webp"
                            ),
                            new PortfolioContentImageResponse(
                                    2222222222222222222L,
                                    "https://cdn.example.com/portfolio/content/2026/06/2222222222222222222.webp"
                            )
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
                    .andExpect(jsonPath("$.thumbnailImageUrl").value("https://cdn.example.com/portfolio/thumbnail/2026/06/7876543210987654321.webp"))
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
                                                    fieldWithPath("thumbnailImageId")
                                                            .type(JsonFieldType.STRING)
                                                            .description("썸네일 이미지의 ImageFileMeta ID (TSID, JSON 문자열). PUT 요청 시 클라이언트가 이 값을 그대로 thumbnailImageId 로 돌려보내야 서버가 동일 썸네일 유지로 인식. 썸네일 미등록 시 null.")
                                                            .optional(),
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
                                                            .description("링크 URL (http:// 또는 https://)"),
                                                    fieldWithPath("contentImages")
                                                            .type(JsonFieldType.ARRAY)
                                                            .description("본문에서 사용 중인 이미지의 (imageId, url) 매핑 목록. 본문에 이미지가 없으면 빈 배열. 클라이언트는 편집 진입 시 이 매핑으로 url→imageId 룩업 테이블을 초기화하고, PUT 요청 시 본문 내 image 노드 src 로부터 imageId 를 역추적해 contentImageIds 를 채워 보냄."),
                                                    fieldWithPath("contentImages[].imageId")
                                                            .type(JsonFieldType.STRING)
                                                            .description("ImageFileMeta ID (TSID, JSON 문자열)"),
                                                    fieldWithPath("contentImages[].url")
                                                            .type(JsonFieldType.STRING)
                                                            .description("이미지의 전체 URL (본문 image 노드 src 와 동일 값)")
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

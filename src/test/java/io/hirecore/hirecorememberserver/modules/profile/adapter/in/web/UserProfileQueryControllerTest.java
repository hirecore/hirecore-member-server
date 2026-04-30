package io.hirecore.hirecorememberserver.modules.profile.adapter.in.web;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import io.hirecore.hirecorememberserver.common.web.SwaggerDocs;
import io.hirecore.hirecorememberserver.common.security.WebMvcSecuritySupport;
import io.hirecore.hirecorememberserver.common.config.StrictJsonConfig;
import io.hirecore.hirecorememberserver.sharedkernel.application.security.AuthPrincipal;
import io.hirecore.hirecorememberserver.sharedkernel.infrastructure.persistence.exception.DataConsistencyException;
import io.hirecore.hirecorememberserver.modules.profile.adapter.in.web.dto.response.UserProfileSummaryApiResponse;
import io.hirecore.hirecorememberserver.modules.profile.adapter.in.web.mapper.UserProfileQueryWebMapper;
import io.hirecore.hirecorememberserver.modules.profile.application.port.in.LoadUserProfileSummaryUseCase;
import io.hirecore.hirecorememberserver.modules.profile.application.port.in.dto.response.UserProfileSummaryResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link UserProfileQueryController} 웹 계층 테스트.
 *
 * <p>보안 필터를 활성화하여 인증 여부에 따른 접근 제어도 함께 검증합니다.
 * {@code @AuthenticationPrincipal}은 Spring Security Test의
 * {@code authentication()} post-processor로 SecurityContext를 설정하여 주입합니다.</p>
 */
@DisplayName("UserProfileQueryController 웹 계층 테스트")
@WebMvcTest(controllers = UserProfileQueryController.class)
@Import({WebMvcSecuritySupport.class, StrictJsonConfig.class})
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class UserProfileQueryControllerTest {

    private static final Long MEMBER_ID = 12345L;
    private static final String EMAIL = "user@hirecore.io";
    private static final String ROLE = "USER";
    private static final String PUBLIC_CODE = "Ab3Xy9Zq";
    private static final String NICKNAME = "은철";
    private static final String PROFILE_IMAGE_URL = "/images/profile/abc123.png";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoadUserProfileSummaryUseCase loadUserProfileSummaryUseCase;

    @MockitoBean
    private UserProfileQueryWebMapper userProfileQueryWebMapper;

    private static UsernamePasswordAuthenticationToken createAuthToken() {
        AuthPrincipal principal = new AuthPrincipal(MEMBER_ID, EMAIL, ROLE, 0);
        return new UsernamePasswordAuthenticationToken(
                principal, null, List.of(new SimpleGrantedAuthority("ROLE_" + ROLE))
        );
    }

    @Nested
    @DisplayName("성공 케이스 (Happy Path)")
    class SuccessTest {

        @Test
        @DisplayName("[200 OK] 인증된 사용자가 요청하면 프로필 요약 정보를 반환한다")
        void should_return_profile_summary_when_authenticated() throws Exception {
            // given
            UserProfileSummaryResponse response = new UserProfileSummaryResponse(
                    String.valueOf(MEMBER_ID), EMAIL, PUBLIC_CODE, NICKNAME, PROFILE_IMAGE_URL
            );
            UserProfileSummaryApiResponse apiResponse = new UserProfileSummaryApiResponse(
                    String.valueOf(MEMBER_ID), EMAIL, PUBLIC_CODE, NICKNAME, PROFILE_IMAGE_URL
            );
            given(loadUserProfileSummaryUseCase.execute(MEMBER_ID, EMAIL)).willReturn(response);
            given(userProfileQueryWebMapper.toApiResponse(response)).willReturn(apiResponse);

            // when & then
            mockMvc.perform(get("/api/user/profile/summary")
                            .with(authentication(createAuthToken())))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(String.valueOf(MEMBER_ID)))
                    .andExpect(jsonPath("$.email").value(EMAIL))
                    .andExpect(jsonPath("$.publicCode").value(PUBLIC_CODE))
                    .andExpect(jsonPath("$.nickname").value(NICKNAME))
                    .andExpect(jsonPath("$.profileImageUrl").value(PROFILE_IMAGE_URL))

                    // 문서화
                    .andDo(document("200-user-profile-summary-success",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag(SwaggerDocs.Tags.Profile.USER_PROFILE)
                                            .summary("\"현재 로그인 사용자 프로필 요약 조회\": 인증된 사용자의 프로필 요약 정보를 반환한다.")
                                            .description("""
                                                    현재 로그인한 사용자의 프로필 요약 정보를 반환합니다.
                                                    프론트엔드에서 페이지 진입 시 로그인 상태를 확인하는 데 사용됩니다.

                                                        [인증 방식]
                                                         - HttpOnly 쿠키의 accessToken (JWT)
                                                         - credentials: "include"로 쿠키 자동 전송

                                                        [응답 필드]
                                                         - id, email: JWT에서 추출 (DB 조회 없음)
                                                         - publicCode, nickname, profileImageUrl: profiles 테이블에서 조회

                                                        [에러 응답]
                                                         - 401 Unauthorized: 쿠키 없음 또는 토큰 만료
                                                    """)
                                            .responseFields(
                                                    fieldWithPath("id")
                                                            .type(JsonFieldType.STRING)
                                                            .description("회원 계정 고유 식별자 (Long → String 변환)"),
                                                    fieldWithPath("email")
                                                            .type(JsonFieldType.STRING)
                                                            .description("회원 이메일"),
                                                    fieldWithPath("publicCode")
                                                            .type(JsonFieldType.STRING)
                                                            .description("프로필 공개 코드 (8자리 고유 문자열)"),
                                                    fieldWithPath("nickname")
                                                            .type(JsonFieldType.STRING)
                                                            .description("회원 닉네임"),
                                                    fieldWithPath("profileImageUrl")
                                                            .type(JsonFieldType.STRING)
                                                            .description("프로필 이미지 저장 경로 (없으면 null)")
                                                            .optional()
                                            )
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("[200 OK] 프로필 이미지가 없는 사용자도 정상 응답한다 (profileImageUrl=null)")
        void should_return_null_profile_image_when_not_set() throws Exception {
            // given
            UserProfileSummaryResponse response = new UserProfileSummaryResponse(
                    String.valueOf(MEMBER_ID), EMAIL, PUBLIC_CODE, NICKNAME, null
            );
            UserProfileSummaryApiResponse apiResponse = new UserProfileSummaryApiResponse(
                    String.valueOf(MEMBER_ID), EMAIL, PUBLIC_CODE, NICKNAME, null
            );
            given(loadUserProfileSummaryUseCase.execute(MEMBER_ID, EMAIL)).willReturn(response);
            given(userProfileQueryWebMapper.toApiResponse(response)).willReturn(apiResponse);

            // when & then
            mockMvc.perform(get("/api/user/profile/summary")
                            .with(authentication(createAuthToken())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nickname").value(NICKNAME))
                    .andExpect(jsonPath("$.profileImageUrl").doesNotExist());
        }
    }

    @Nested
    @DisplayName("인증 실패 케이스")
    class AuthenticationFailureTest {

        @Test
        @DisplayName("[401 Unauthorized] 인증 토큰 없이 요청하면 401을 반환한다")
        void should_return_401_when_not_authenticated() throws Exception {
            // when & then
            mockMvc.perform(get("/api/user/profile/summary"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("비즈니스 로직 실패 케이스")
    class BusinessLogicFailureTest {

        @Test
        @DisplayName("[500 Internal Server Error] 프로필이 존재하지 않으면 데이터 정합성 에러를 반환한다")
        void should_return_500_when_profile_not_found() throws Exception {
            // given
            given(loadUserProfileSummaryUseCase.execute(MEMBER_ID, EMAIL))
                    .willThrow(new DataConsistencyException(
                            "데이터 정합성 오류: MemberAccount(ID: 12345)에 대한 프로필이 존재하지 않습니다."
                    ));

            // when & then
            mockMvc.perform(get("/api/user/profile/summary")
                            .with(authentication(createAuthToken())))
                    .andDo(print())
                    .andExpect(status().isInternalServerError());
        }
    }
}

package io.hirecore.hirecorememberserver.modules.account.adapter.in.web;

import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.SimpleType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hirecore.hirecorememberserver.common.adapter.in.SwaggerDocs;
import io.hirecore.hirecorememberserver.common.adapter.in.config.StrictJsonConfig;
import io.hirecore.hirecorememberserver.common.exception.GlobalExceptionCodeCluster;
import io.hirecore.hirecorememberserver.common.adapter.in.WebMvcSecuritySupport;
import io.hirecore.hirecorememberserver.modules.account.adapter.in.web.mapper.SocialLoginWebMapper;
import io.hirecore.hirecorememberserver.common.utils.AuthCookieUtils;
import io.hirecore.hirecorememberserver.modules.account.adapter.in.web.mapper.SocialLoginWebMapperImpl;
import io.hirecore.hirecorememberserver.modules.account.application.port.in.dto.request.SocialLoginCommand;
import io.hirecore.hirecorememberserver.modules.account.application.port.in.dto.response.PairTokenResponse;
import io.hirecore.hirecorememberserver.modules.account.application.port.in.dto.response.fixture.PairTokenResponseFixture;
import io.hirecore.hirecorememberserver.modules.account.application.exception.SocialAccountApplicationException;
import io.hirecore.hirecorememberserver.modules.account.application.exception.SocialAccountApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.common.application.exception.ApplicationExceptionCode;
import io.hirecore.hirecorememberserver.modules.account.domain.exception.SocialAccountDomainException;
import io.hirecore.hirecorememberserver.modules.account.domain.exception.SocialAccountDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.account.application.port.in.LoginSocialUserUseCase;
import io.hirecore.hirecorememberserver.modules.account.application.port.in.LogoutUseCase;
import io.hirecore.hirecorememberserver.sharedkernel.vo.OAuth2Provider;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("AuthController 웹 계층 테스트")
@WebMvcTest(controllers = AuthController.class)
@Import({
        WebMvcSecuritySupport.class,
        StrictJsonConfig.class,
        SocialLoginWebMapperImpl.class
})
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LoginSocialUserUseCase loginSocialUserUseCase;

    @Autowired
    private SocialLoginWebMapper socialDtoMapper;

    @MockitoBean
    private AuthCookieUtils authCookieUtils;

    @MockitoBean
    private LogoutUseCase logoutUseCase;

    // ──────────────────────────────────────────────
    //  소셜 로그인 테스트
    // ──────────────────────────────────────────────

    @Nested
    @DisplayName("소셜 로그인: 성공 케이스 (Happy Path)")
    class SocialLoginSuccessTest {

        @Test
        @DisplayName("[200 OK] 올바른 소셜 로그인 요청 시 HttpOnly 쿠키에 토큰을 반환한다.")
        void social_login_success() throws Exception {
            // given
            Map<String, String> requestMap = new HashMap<>();
            requestMap.put("authorizationCode", "real_auth_code_123");
            String requestBody = objectMapper.writeValueAsString(requestMap);

            PairTokenResponse fakeTokens = PairTokenResponseFixture.createValidResponse();

            given(loginSocialUserUseCase.execute(
                    argThat(req -> req.authorizationCode().equals("real_auth_code_123"))
            )).willReturn(fakeTokens);

            given(authCookieUtils.createAccessTokenCookie(fakeTokens.accessToken()))
                    .willReturn(ResponseCookie.from("accessToken", fakeTokens.accessToken())
                            .httpOnly(true)
                            .secure(true)
                            .sameSite("Strict")
                            .path("/")
                            .maxAge(3 * 60 * 60)
                            .build()
                    );
            given(authCookieUtils.createRefreshTokenCookie(fakeTokens.refreshToken()))
                    .willReturn(ResponseCookie.from("refreshToken", fakeTokens.refreshToken())
                            .httpOnly(true)
                            .secure(true)
                            .sameSite("Strict")
                            .path("/")
                            .maxAge(7 * 24 * 60 * 60)
                            .build()
                    );

            // when & then
            mockMvc.perform(post("/api/auth/login/user/{provider}", OAuth2Provider.KAKAO.toString().toLowerCase())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(cookie().exists("accessToken"))
                    .andExpect(cookie().httpOnly("accessToken", true))
                    .andExpect(cookie().secure("accessToken", true))
                    .andExpect(cookie().maxAge("accessToken", 10800))

                    .andExpect(cookie().exists("refreshToken"))
                    .andExpect(cookie().httpOnly("refreshToken", true))
                    .andExpect(cookie().secure("refreshToken", true))
                    .andExpect(cookie().maxAge("refreshToken", 604800))

                    // 문서화
                    .andDo(document("200-user-login-social-success",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag(SwaggerDocs.Tags.Account.AUTH)
                                            .summary("\"일반 사용자 소셜 프로바이더 로그인\": 일반 사용자는 소셜 프로바이더 로그인을 할 수 있다.")
                                            .description("""
                                                    소셜 인가 코드를 전달받아 사용자 인증을 처리하고,
                                                    JWT 토큰 쌍(accessToken, refreshToken)을 HttpOnly 쿠키로 발급합니다.

                                                        [처리 흐름]
                                                         1. URL 경로의 provider로 소셜 로그인 플랫폼을 결정합니다. (현재 kakao만 지원)
                                                         2. 인가 코드로 해당 소셜 API를 통해 사용자 프로필을 조회합니다.
                                                         3. 기존 회원이면 기존 계정으로 토큰을 발급합니다.
                                                         4. 신규 회원이면 계정을 생성한 후 토큰을 발급합니다.

                                                        [응답 방식]
                                                         - 200 OK: 응답 본문 없이 Set-Cookie 헤더로 토큰이 전달됩니다.
                                                         - accessToken: HttpOnly, Secure, SameSite=Strict, Path=/, Max-Age=10800 (3시간)
                                                         - refreshToken: HttpOnly, Secure, SameSite=Strict, Path=/, Max-Age=604800 (7일)

                                                        [에러 응답]
                                                         - 400 PROVIDER_INVALID: 지원하지 않는 provider (예: naver)
                                                         - 400 REQUEST_VALUE_INVALID: authorizationCode 누락
                                                         - 500 EMAIL_MISSING 등: 소셜 프로필 데이터 불완전
                                                         - 500 SERVER_INTERNAL_ERROR: 외부 API 호출 실패 등

                                                        [특이 사항]
                                                         - authorizationCode는 소셜 인가 서버가 발급한 일회성 코드입니다.
                                                         - 이미 사용된 인가 코드를 재전송하면 500 에러가 발생할 수 있습니다.
                                                    """)
                                            .pathParameters(
                                                    ResourceDocumentation.parameterWithName("provider")
                                                            .type(SimpleType.STRING)
                                                            .description("소셜 로그인 플랫폼 제공자 (지원: kakao, naver, google)")
                                            )
                                            .requestFields(
                                                    fieldWithPath("authorizationCode")
                                                            .type(JsonFieldType.STRING)
                                                            .description("소셜 인증 서버에서 발급받은 인가 코드 (일회성). 빈 문자열 불가.")
                                            )
                                            .responseHeaders(
                                                    headerWithName("Set-Cookie")
                                                            .description("accessToken 및 refreshToken이 HttpOnly, Secure, SameSite=Strict 속성의 쿠키로 설정됩니다. accessToken Max-Age=10800(3시간), refreshToken Max-Age=604800(7일)")
                                            )
                                            .build()
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("소셜 로그인: 입력값 검증 실패 케이스 (Web Layer Validation)")
    class SocialLoginValidationFailureTest {

        @Test
        @DisplayName("[400 Bad Request] 인가코드가 누락되면 @Valid 검증 실패로 에러를 반환한다.")
        void social_login_validation_authorizationCode_missing() throws Exception {
            // given
            Map<String, String> requestMap = new HashMap<>();
            requestMap.put("authorizationCode", "");
            String requestBody = objectMapper.writeValueAsString(requestMap);

            // when & then
            mockMvc.perform(post("/api/auth/login/user/{provider}", OAuth2Provider.KAKAO.toString().toLowerCase())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value(GlobalExceptionCodeCluster.Interface.REQUEST_VALUE_INVALID.getCode()))
                    .andExpect(jsonPath("$.fieldErrors[0].field").value("authorizationCode"))
                    .andExpect(jsonPath("$.fieldErrors[0].reason").value("인가 코드는 필수 값입니다."))

                    // [문서화] 입력값 검증 실패 시의 응답 포맷
                    .andDo(document("400-user-login-social-error-validation",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag(SwaggerDocs.Tags.Account.AUTH)
                                            .requestFields(
                                                    fieldWithPath("authorizationCode")
                                                            .type(JsonFieldType.STRING)
                                                            .description("소셜 인가 코드 (빈 문자열 전달 시 검증 실패)")
                                            )
                                            .responseFields(
                                                    fieldWithPath("timestamp")
                                                            .type(JsonFieldType.STRING)
                                                            .description("에러 발생 시각 (ISO-8601 형식)"),
                                                    fieldWithPath("trackingId")
                                                            .type(JsonFieldType.STRING)
                                                            .description("에러 추적용 고유 ID (로그 조회 시 활용)"),
                                                    fieldWithPath("errorCode")
                                                            .type(JsonFieldType.STRING)
                                                            .description("에러 코드. 입력값 검증 실패 시 항상 `REQUEST_VALUE_INVALID`"),
                                                    fieldWithPath("message")
                                                            .type(JsonFieldType.STRING)
                                                            .description("사용자 안내 메시지: '입력된 요청값에 오류가 있습니다.'"),
                                                    fieldWithPath("path")
                                                            .type(JsonFieldType.STRING)
                                                            .description("에러가 발생한 요청 URI 경로"),
                                                    fieldWithPath("fieldErrors")
                                                            .type(JsonFieldType.ARRAY)
                                                            .description("검증 실패한 필드 목록. 각 항목에 field(필드명)와 reason(사유) 포함")
                                                            .optional(),
                                                    fieldWithPath("fieldErrors[].field")
                                                            .type(JsonFieldType.STRING)
                                                            .description("검증 실패한 필드명 (예: authorizationCode)")
                                                            .optional(),
                                                    fieldWithPath("fieldErrors[].reason")
                                                            .type(JsonFieldType.STRING)
                                                            .description("해당 필드의 검증 실패 사유")
                                                            .optional(),
                                                    fieldWithPath("attributes")
                                                            .type(JsonFieldType.OBJECT)
                                                            .description("부가 데이터 (검증 실패 시 보통 null)")
                                                            .optional()
                                            )
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("[400 Bad Request] 지원하지 않는 Provider를 경로에 입력하면 PROVIDER_INVALID 에러가 발생한다")
        void social_login_validation_invalid_provider() throws Exception {
            // given
            Map<String, String> requestMap = new HashMap<>();
            requestMap.put("authorizationCode", "code");
            String requestBody = objectMapper.writeValueAsString(requestMap);

            given(loginSocialUserUseCase.execute(any(SocialLoginCommand.class)))
                    .willThrow(new SocialAccountDomainException(
                            SocialAccountDomainExceptionCodeCluster.DetailResponse.PROVIDER_INVALID));

            // when & then
            mockMvc.perform(post("/api/auth/login/user/NOT_SUPPORT_PROVIDER")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value(
                            SocialAccountDomainExceptionCodeCluster.DetailResponse.PROVIDER_INVALID.getErrorCode()));
        }
    }

    @Nested
    @DisplayName("소셜 로그인: 비즈니스 로직 실패 케이스 (Business Logic Failure)")
    class SocialLoginBusinessLogicFailureTest {

        @Test
        @DisplayName("[500 Internal Server Error] 소셜 프로필에 이메일이 없는 등 비즈니스 로직 실패 시 에러를 반환한다.")
        void social_login_business_error_email_missing() throws Exception {
            // given
            Map<String, String> requestMap = new HashMap<>();
            requestMap.put("authorizationCode", "valid_code_but_no_email");
            String requestBody = objectMapper.writeValueAsString(requestMap);

            ApplicationExceptionCode applicationError = SocialAccountApplicationExceptionCodeCluster.DetailResponse.USER_PROFILE_EMAIL_MISSING;

            given(loginSocialUserUseCase.execute(any(SocialLoginCommand.class)))
                    .willThrow(new SocialAccountApplicationException(applicationError));

            // when & then
            mockMvc.perform(post("/api/auth/login/user/{provider}", OAuth2Provider.KAKAO.toString().toLowerCase())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.errorCode").value(applicationError.getErrorCode()))
                    .andExpect(jsonPath("$.message").value(applicationError.getClientMessage()))

                    // [문서화] 비즈니스 에러 응답 포맷
                    .andDo(document("500-user-login-social-error-business",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag(SwaggerDocs.Tags.Account.AUTH)
                                            .requestFields(
                                                    fieldWithPath("authorizationCode")
                                                            .type(JsonFieldType.STRING)
                                                            .description("소셜 인가 코드")
                                            )
                                            .responseFields(
                                                    fieldWithPath("timestamp")
                                                            .type(JsonFieldType.STRING)
                                                            .description("에러 발생 시각 (ISO-8601 형식)"),
                                                    fieldWithPath("trackingId")
                                                            .type(JsonFieldType.STRING)
                                                            .description("에러 추적용 고유 ID"),
                                                    fieldWithPath("errorCode")
                                                            .type(JsonFieldType.STRING)
                                                            .description("애플리케이션 에러 코드 (예: USER_PROFILE_EMAIL_MISSING)"),
                                                    fieldWithPath("message")
                                                            .type(JsonFieldType.STRING)
                                                            .description("사용자 안내 메시지 (예: '소셜 프로필 정보 로드 중 서버 오류가 발생했습니다.')"),
                                                    fieldWithPath("path")
                                                            .type(JsonFieldType.STRING)
                                                            .description("에러가 발생한 요청 URI 경로"),
                                                    fieldWithPath("attributes")
                                                            .type(JsonFieldType.OBJECT)
                                                            .description("에러 관련 부가 데이터 (도메인 예외의 context 맵)")
                                                            .optional()
                                            )
                                            .build()
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("소셜 로그인: 엣지 케이스 (Edge Cases)")
    class SocialLoginEdgeCaseTest {

        @Test
        @DisplayName("[400 Bad Request] 인가코드가 누락된 경우(필드 자체 없음) 400 에러가 발생해야 한다.")
        void social_login_validation_authorizationCode_field_missing() throws Exception {
            // given
            Map<String, String> requestMap = new HashMap<>();
            String requestBody = objectMapper.writeValueAsString(requestMap);

            // when & then
            mockMvc.perform(post("/api/auth/login/user/kakao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").exists());
        }

        @Test
        @DisplayName("[400 Bad Request] 인가코드 타입이 틀리면(Int -> String) JSON 파싱 에러가 발생한다.")
        void social_login_validation_incorrect_type() throws Exception {
            // given
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("authorizationCode", 12345);
            String requestBody = objectMapper.writeValueAsString(requestMap);

            // when & then
            mockMvc.perform(post("/api/auth/login/user/kakao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").exists());
        }
    }

    // ──────────────────────────────────────────────
    //  로그아웃 테스트
    // ──────────────────────────────────────────────

    @Nested
    @DisplayName("로그아웃: 성공 케이스 (Happy Path)")
    class LogoutSuccessTest {

        @Test
        @DisplayName("[200 OK] accessToken 쿠키가 존재하면 블랙리스트 등록 후 만료 쿠키를 반환한다.")
        void logout_success_with_access_token_cookie() throws Exception {
            // given
            given(authCookieUtils.createExpiredAccessTokenCookie())
                    .willReturn(ResponseCookie.from("accessToken", "")
                            .httpOnly(true).secure(true).sameSite("Strict").path("/").maxAge(0).build());
            given(authCookieUtils.createExpiredRefreshTokenCookie())
                    .willReturn(ResponseCookie.from("refreshToken", "")
                            .httpOnly(true).secure(true).sameSite("Strict").path("/").maxAge(0).build());

            // when & then
            mockMvc.perform(post("/api/auth/logout")
                            .cookie(new Cookie("accessToken", "test-access-token")))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(cookie().exists("accessToken"))
                    .andExpect(cookie().maxAge("accessToken", 0))
                    .andExpect(cookie().exists("refreshToken"))
                    .andExpect(cookie().maxAge("refreshToken", 0))

                    // 문서화
                    .andDo(document("200-user-logout-success",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag(SwaggerDocs.Tags.Account.AUTH)
                                            .summary("\"로그아웃\": 인증된 사용자의 토큰을 무효화하고 쿠키를 삭제한다.")
                                            .description("""
                                                    로그아웃 시 서버에서 수행하는 작업:

                                                        [처리 흐름]
                                                         1. 요청 쿠키에서 accessToken을 추출합니다.
                                                         2. 해당 토큰을 Redis 블랙리스트에 등록합니다 (남은 만료 시간을 TTL로 설정).
                                                         3. accessToken, refreshToken 쿠키를 maxAge=0으로 설정하여 브라우저에서 삭제합니다.

                                                        [응답 방식]
                                                         - 200 OK: 응답 본문 없이 Set-Cookie 헤더로 만료된 쿠키가 전달됩니다.

                                                        [특이 사항]
                                                         - 블랙리스트에 등록된 토큰은 JwtAuthenticationFilter에서 인증이 거부됩니다.
                                                         - 토큰 만료 시 Redis에서 자동 삭제되어 메모리를 절약합니다.
                                                         - accessToken 쿠키가 없어도 200 OK를 반환합니다 (쿠키 삭제만 수행).
                                                    """)
                                            .responseHeaders(
                                                    headerWithName("Set-Cookie")
                                                            .description("accessToken, refreshToken 쿠키가 maxAge=0으로 설정되어 브라우저에서 삭제됩니다.")
                                            )
                                            .build()
                            )
                    ));

            verify(logoutUseCase).execute("test-access-token");
        }

        @Test
        @DisplayName("[200 OK] accessToken 쿠키가 없어도 만료 쿠키를 반환한다 (UseCase 호출 없음).")
        void logout_success_without_access_token_cookie() throws Exception {
            // given
            given(authCookieUtils.createExpiredAccessTokenCookie())
                    .willReturn(ResponseCookie.from("accessToken", "")
                            .httpOnly(true).secure(true).sameSite("Strict").path("/").maxAge(0).build());
            given(authCookieUtils.createExpiredRefreshTokenCookie())
                    .willReturn(ResponseCookie.from("refreshToken", "")
                            .httpOnly(true).secure(true).sameSite("Strict").path("/").maxAge(0).build());

            // when & then
            mockMvc.perform(post("/api/auth/logout"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(cookie().exists("accessToken"))
                    .andExpect(cookie().maxAge("accessToken", 0))
                    .andExpect(cookie().exists("refreshToken"))
                    .andExpect(cookie().maxAge("refreshToken", 0));

            verify(logoutUseCase, never()).execute(any());
        }
    }
}

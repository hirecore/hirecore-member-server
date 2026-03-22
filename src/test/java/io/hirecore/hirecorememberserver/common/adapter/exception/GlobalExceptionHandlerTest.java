package io.hirecore.hirecorememberserver.common.adapter.exception;

import io.hirecore.hirecorememberserver.common.adapter.in.logging.StructuredErrorLogger;
import io.hirecore.hirecorememberserver.common.application.exception.ApplicationExceptionCode;
import io.hirecore.hirecorememberserver.common.application.exception.BaseApplicationException;
import io.hirecore.hirecorememberserver.common.application.port.out.TokenBlacklistPort;
import io.hirecore.hirecorememberserver.common.application.port.out.TokenResolverPort;
import io.hirecore.hirecorememberserver.common.adapter.out.persistence.exception.DataConsistencyException;
import io.hirecore.hirecorememberserver.common.domain.exception.BaseDomainException;
import io.hirecore.hirecorememberserver.common.domain.exception.DomainExceptionCode;
import io.hirecore.hirecorememberserver.common.exception.GlobalExceptionHandler;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("GlobalExceptionHandler 슬라이스 테스트")
@WebMvcTest(
        controllers = GlobalExceptionHandlerTest.ExceptionTriggerController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
@Import({GlobalExceptionHandler.class, GlobalExceptionHandlerTest.ExceptionTriggerController.class})
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StructuredErrorLogger structuredErrorLogger;

    // JwtAuthenticationFilter(@Component)가 @WebMvcTest 스캔 대상에 포함되므로
    // 해당 필터의 의존성인 TokenResolverPort, TokenBlacklistPort를 모킹하여 컨텍스트 로딩을 허용한다.
    @MockitoBean
    private TokenResolverPort tokenResolverPort;

    @MockitoBean
    private TokenBlacklistPort tokenBlacklistPort;

    // =========================================================================
    // 테스트 전용 예외 코드 (계층 경계 위반 방지 — modules 패키지 임포트 금지)
    // GlobalExceptionHandler가 BaseDomainException / BaseApplicationException 계열을
    // 범용적으로 처리함을 검증하기 위해 테스트 전용 구현체를 선언한다.
    // =========================================================================

    enum TestDomainErrorCode implements DomainExceptionCode {
        TEST_DOMAIN_ERROR;

        @Override public HttpStatus getHttpStatus()    { return HttpStatus.INTERNAL_SERVER_ERROR; }
        @Override public String getErrorCode()         { return name(); }
        @Override public String getLogMessage()        { return "테스트 도메인 예외 로그"; }
        @Override public String getClientMessage()     { return "테스트 도메인 예외 메시지"; }
    }

    static class TestDomainException extends BaseDomainException {
        TestDomainException() {
            super("TEST", TestDomainErrorCode.TEST_DOMAIN_ERROR);
        }
    }

    enum TestApplicationErrorCode implements ApplicationExceptionCode {
        TEST_APPLICATION_ERROR;

        @Override public HttpStatus getHttpStatus()    { return HttpStatus.BAD_REQUEST; }
        @Override public String getErrorCode()         { return name(); }
        @Override public String getLogMessage()        { return "테스트 애플리케이션 예외 로그"; }
        @Override public String getClientMessage()     { return "테스트 애플리케이션 예외 메시지"; }
    }

    static class TestApplicationException extends BaseApplicationException {
        TestApplicationException() {
            super("TEST", TestApplicationErrorCode.TEST_APPLICATION_ERROR);
        }
    }

    // =========================================================================
    // 예외를 유발하는 테스트 전용 컨트롤러
    // =========================================================================

    @RestController
    static class ExceptionTriggerController {

        @GetMapping("/test/domain-exception")
        public void throwDomainException() {
            throw new TestDomainException();
        }

        @GetMapping("/test/application-exception")
        public void throwApplicationException() {
            throw new TestApplicationException();
        }

        @GetMapping("/test/data-integrity")
        public void throwDataIntegrityViolation() {
            throw new DataIntegrityViolationException("Duplicate entry");
        }

        @GetMapping("/test/data-consistency")
        public void throwDataConsistencyException() {
            throw new DataConsistencyException("Logical data inconsistency");
        }

        @GetMapping("/test/general-exception")
        public void throwGeneralException() {
            throw new RuntimeException("Unexpected error");
        }

        @GetMapping("/test/constraint-violation")
        public void throwConstraintViolation() {
            throw new ConstraintViolationException("Constraint violated", Set.of());
        }

        @PostMapping("/test/validation")
        public void validateRequestBody(@Valid @RequestBody ValidatableRequest request) {
        }

        record ValidatableRequest(@NotBlank(message = "이름은 필수입니다.") String name) {}
    }

    // =========================================================================
    // 테스트: 도메인 예외 (BaseDomainException)
    // =========================================================================

    @Nested
    @DisplayName("도메인 예외 (BaseDomainException)")
    class DomainExceptionTest {

        @Test
        @DisplayName("도메인 예외는 해당 에러코드와 HTTP 상태를 반환한다")
        void should_return_domain_error_code_and_status_for_domain_exception() throws Exception {
            mockMvc.perform(get("/test/domain-exception"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.errorCode").value("TEST_DOMAIN_ERROR"))
                    .andExpect(jsonPath("$.trackingId").isNotEmpty())
                    .andExpect(jsonPath("$.message").isNotEmpty());
        }
    }

    // =========================================================================
    // 테스트: 애플리케이션 예외 (BaseApplicationException)
    // =========================================================================

    @Nested
    @DisplayName("애플리케이션 예외 (BaseApplicationException)")
    class ApplicationExceptionTest {

        @Test
        @DisplayName("애플리케이션 예외는 해당 에러코드와 HTTP 상태를 반환한다")
        void should_return_application_error_code_and_status_for_application_exception() throws Exception {
            mockMvc.perform(get("/test/application-exception"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("TEST_APPLICATION_ERROR"))
                    .andExpect(jsonPath("$.trackingId").isNotEmpty())
                    .andExpect(jsonPath("$.message").isNotEmpty());
        }
    }

    // =========================================================================
    // 테스트: 입력값 검증 실패
    // =========================================================================

    @Nested
    @DisplayName("입력값 검증 실패")
    class ValidationExceptionTest {

        @Test
        @DisplayName("@Valid 검증 실패는 400과 REQUEST_VALUE_INVALID를 반환하고 fieldErrors를 포함한다")
        void should_return_400_and_field_errors_for_validation_failure() throws Exception {
            mockMvc.perform(post("/test/validation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\": \"\"}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("REQUEST_VALUE_INVALID"))
                    .andExpect(jsonPath("$.fieldErrors").isArray())
                    .andExpect(jsonPath("$.fieldErrors[0].field").value("name"))
                    .andExpect(jsonPath("$.fieldErrors[0].reason").value("이름은 필수입니다."));
        }

        @Test
        @DisplayName("ConstraintViolationException은 400과 REQUEST_CONSTRAINT_VIOLATED를 반환한다")
        void should_return_400_and_constraint_violated_error_code() throws Exception {
            mockMvc.perform(get("/test/constraint-violation"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("REQUEST_CONSTRAINT_VIOLATED"))
                    .andExpect(jsonPath("$.trackingId").isNotEmpty());
        }
    }

    // =========================================================================
    // 테스트: JSON 파싱 오류
    // =========================================================================

    @Nested
    @DisplayName("JSON 파싱 오류")
    class JsonParsingExceptionTest {

        @Test
        @DisplayName("문법 오류가 있는 JSON 바디는 400과 JSON_GRAMMAR_INVALID를 반환한다")
        void should_return_400_and_grammar_invalid_for_malformed_json() throws Exception {
            mockMvc.perform(post("/test/validation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ malformed json "))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("JSON_GRAMMAR_INVALID"));
        }
    }

    // =========================================================================
    // 테스트: 데이터 계층 예외
    // =========================================================================

    @Nested
    @DisplayName("데이터 계층 예외")
    class DataLayerExceptionTest {

        @Test
        @DisplayName("DataIntegrityViolationException은 409 Conflict와 SERVER_DATA_INTEGRITY_VIOLATION을 반환한다")
        void should_return_409_and_integrity_error_code_for_data_integrity_violation() throws Exception {
            mockMvc.perform(get("/test/data-integrity"))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.errorCode").value("SERVER_DATA_INTEGRITY_VIOLATION"))
                    .andExpect(jsonPath("$.trackingId").isNotEmpty());
        }

        @Test
        @DisplayName("DataConsistencyException은 500과 SERVER_DATA_INCONSISTENT를 반환한다")
        void should_return_500_and_inconsistency_error_code_for_data_consistency_exception() throws Exception {
            mockMvc.perform(get("/test/data-consistency"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.errorCode").value("SERVER_DATA_INCONSISTENT"))
                    .andExpect(jsonPath("$.trackingId").isNotEmpty());
        }
    }

    // =========================================================================
    // 테스트: 서버 폴백
    // =========================================================================

    @Nested
    @DisplayName("서버 폴백")
    class ServerFallbackTest {

        @Test
        @DisplayName("처리되지 않은 예외는 500과 SERVER_INTERNAL_ERROR를 반환한다")
        void should_return_500_and_internal_error_code_for_unhandled_exception() throws Exception {
            mockMvc.perform(get("/test/general-exception"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.errorCode").value("SERVER_INTERNAL_ERROR"))
                    .andExpect(jsonPath("$.trackingId").isNotEmpty());
        }

        @Test
        @DisplayName("존재하지 않는 경로는 404와 RESOURCE_NOT_FOUND를 반환한다")
        void should_return_404_and_not_found_error_code_for_unknown_path() throws Exception {
            mockMvc.perform(get("/non-existent-path"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value("RESOURCE_NOT_FOUND"));
        }
    }
}

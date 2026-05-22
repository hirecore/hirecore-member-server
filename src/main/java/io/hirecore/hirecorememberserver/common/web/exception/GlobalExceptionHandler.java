package io.hirecore.hirecorememberserver.common.web.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import io.hirecore.hirecorememberserver.common.web.logging.StructuredErrorLogger;
import io.hirecore.hirecorememberserver.sharedkernel.application.exception.BaseApplicationException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.BaseDomainException;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.exception.DataConsistencyException;
import org.springframework.dao.DataIntegrityViolationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final StructuredErrorLogger logger;
    private static final String TRACE_ID_KEY = "traceId";

    // 1. Domain Exception
    @ExceptionHandler(BaseDomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainLayerException(
            BaseDomainException e, HttpServletRequest request
    ) {
        return buildErrorResponse(
                e.getErrorCode(),
                e.getClientMessage(),
                e.getHttpStatus().value(),
                null,
                e.getContext(),
                null,
                request,
                e
        );
    }

    // 2. Application Exception
    @ExceptionHandler(BaseApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationLayerException(
            BaseApplicationException e, HttpServletRequest request
    ) {
        return buildErrorResponse(
                e.getErrorCode(),
                e.getClientMessage(),
                e.getHttpStatus().value(),
                null,
                e.getContext(),
                null,
                request,
                e);
    }

    // 3. Validation Exception (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e, HttpServletRequest request
    ) {
        GlobalExceptionCodeCluster.Interface errorCode = GlobalExceptionCodeCluster.Interface.REQUEST_VALUE_INVALID;

        List<ErrorResponse.FieldErrorDetail> fieldErrors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> ErrorResponse.FieldErrorDetail.builder()
                        .field(error.getField())
                        .reason(error.getDefaultMessage())
                        .build())
                .toList();

        Map<String, Object> serverMetadata = Map.of(
                "errorCount", fieldErrors.size(),
                "firstErrorField", fieldErrors.isEmpty() ? "" : fieldErrors.get(0).getField()
        );

        return buildErrorResponse(errorCode.getCode(), errorCode.getMessage(), errorCode.getHttpStatus().value(), fieldErrors, null, serverMetadata, request, e);
    }

    // 4. Constraint Violation (@Validated)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException e, HttpServletRequest request
    ) {
        GlobalExceptionCodeCluster.Interface errorCode = GlobalExceptionCodeCluster.Interface.REQUEST_CONSTRAINT_VIOLATED;

        List<ErrorResponse.FieldErrorDetail> fieldErrors = e.getConstraintViolations().stream()
                .map(violation -> ErrorResponse.FieldErrorDetail.builder()
                        .field(getSimplePropertyName(violation.getPropertyPath().toString()))
                        .reason(violation.getMessage())
                        .build())
                .toList();

        Map<String, Object> serverMetadata = Map.of("rawMessage", e.getMessage());

        return buildErrorResponse(errorCode.getCode(), errorCode.getMessage(), errorCode.getHttpStatus().value(), fieldErrors, null, serverMetadata, request, e);
    }

    // 4-1. Missing Required Request Parameter (@RequestParam required)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e, HttpServletRequest request
    ) {
        GlobalExceptionCodeCluster.Interface errorCode = GlobalExceptionCodeCluster.Interface.REQUEST_VALUE_INVALID;

        List<ErrorResponse.FieldErrorDetail> fieldErrors = List.of(
                ErrorResponse.FieldErrorDetail.builder()
                        .field(e.getParameterName())
                        .reason("필수 요청 파라미터가 누락되었습니다.")
                        .build()
        );

        return buildErrorResponse(errorCode.getCode(), errorCode.getMessage(), errorCode.getHttpStatus().value(), fieldErrors, null, null, request, e);
    }

    // 4-2. Type Mismatch on Request Parameter or Path Variable
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e, HttpServletRequest request
    ) {
        GlobalExceptionCodeCluster.Interface errorCode = GlobalExceptionCodeCluster.Interface.REQUEST_VALUE_INVALID;

        String requiredType = e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown";
        List<ErrorResponse.FieldErrorDetail> fieldErrors = List.of(
                ErrorResponse.FieldErrorDetail.builder()
                        .field(e.getName())
                        .reason(String.format("'%s' 타입이어야 합니다.", requiredType))
                        .build()
        );

        return buildErrorResponse(errorCode.getCode(), errorCode.getMessage(), errorCode.getHttpStatus().value(), fieldErrors, null, null, request, e);
    }

    // 5. JSON Parsing Exception
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e, HttpServletRequest request
    ) {
        Throwable cause = e.getCause();

        // 도메인 예외가 래핑된 경우(VO 생성 실패 등) 즉시 도메인 예외 핸들러로 위임하여 처리
        if (cause instanceof ValueInstantiationException valueInstantiationEx &&
                valueInstantiationEx.getCause() instanceof BaseDomainException baseEx) {

            List<ErrorResponse.FieldErrorDetail> fieldErrors = List.of(
                    ErrorResponse.FieldErrorDetail.builder()
                            .field(getJsonFieldName(valueInstantiationEx.getPath()))
                            .reason(baseEx.getClientMessage())
                            .build()
            );
            return buildErrorResponse(baseEx.getErrorCode(), baseEx.getClientMessage(), HttpStatus.BAD_REQUEST.value(), fieldErrors, null, null, request, baseEx);
        }

        // 일반적인 Jackson 파싱 예외는 가독성을 위해 별도 메서드로 추출
        return handleJacksonParsingDetail(cause, request, e);
    }

    // 6. 404 Not Found
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(
            NoResourceFoundException e, HttpServletRequest request
    ) {
        GlobalExceptionCodeCluster.Interface errorCode = GlobalExceptionCodeCluster.Interface.RESOURCE_NOT_FOUND;
        return buildErrorResponse(errorCode.getCode(), errorCode.getMessage(), errorCode.getHttpStatus().value(), null, null, null, request, e);
    }

    // 7. Data Integrity Violation (DB 유니크 제약 위반 등)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
            DataIntegrityViolationException e, HttpServletRequest request
    ) {
        GlobalExceptionCodeCluster.Server errorCode = GlobalExceptionCodeCluster.Server.SERVER_DATA_INTEGRITY_VIOLATION;
        return buildErrorResponse(errorCode.getCode(), errorCode.getMessage(), errorCode.getHttpStatus().value(), null, null, null, request, e);
    }

    // 8. Data Consistency (논리적 정합성 오류)
    @ExceptionHandler(DataConsistencyException.class)
    public ResponseEntity<ErrorResponse> handleDataConsistencyException(
            DataConsistencyException e, HttpServletRequest request
    ) {
        GlobalExceptionCodeCluster.Server errorCode = GlobalExceptionCodeCluster.Server.SERVER_DATA_INCONSISTENT;
        Map<String, Object> serverMetadata = Map.of("severity", "CRITICAL");
        return buildErrorResponse(errorCode.getCode(), errorCode.getMessage(), errorCode.getHttpStatus().value(), null, null, serverMetadata, request, e);
    }

    // 9. Global Exception (최후의 방어선)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception e, HttpServletRequest request
    ) {
        GlobalExceptionCodeCluster.Server errorCode = GlobalExceptionCodeCluster.Server.SERVER_INTERNAL_ERROR;
        return buildErrorResponse(errorCode.getCode(), errorCode.getMessage(), errorCode.getHttpStatus().value(), null, null, null, request, e);
    }

    // ===================================================================================
    // Helpers (유틸리티 메서드)
    // ===================================================================================

    /**
     * 길고 복잡한 Jackson 예외 체인을 분석하여 적절한 에러 응답을 반환합니다.
     */
    private ResponseEntity<ErrorResponse> handleJacksonParsingDetail(
            Throwable cause, HttpServletRequest request, HttpMessageNotReadableException e
    ) {
        GlobalExceptionCodeCluster.Interface errorCode = GlobalExceptionCodeCluster.Interface.JSON_FORMAT_INVALID;
        String errorMessage = errorCode.getMessage();
        List<ErrorResponse.FieldErrorDetail> fieldErrors = new ArrayList<>();
        String debugDetail = e.getMessage();

        if (cause instanceof JsonParseException) {
            errorCode = GlobalExceptionCodeCluster.Interface.JSON_GRAMMAR_INVALID;
            errorMessage = errorCode.getMessage();
        } else if (cause instanceof InvalidFormatException invalidEx) {
            errorCode = GlobalExceptionCodeCluster.Interface.JSON_TYPE_INVALID;
            String fieldName = getJsonFieldName(invalidEx.getPath());
            String targetTypeName = invalidEx.getTargetType().getSimpleName();
            errorMessage = String.format(errorCode.getMessage(), targetTypeName);
            fieldErrors.add(ErrorResponse.FieldErrorDetail.builder().field(fieldName).reason(errorMessage).build());
            debugDetail = String.format("Field: %s, Expected: %s, Value: %s", fieldName, targetTypeName, invalidEx.getValue());
        } else if (cause instanceof UnrecognizedPropertyException unrecognizedEx) {
            errorCode = GlobalExceptionCodeCluster.Interface.JSON_PROPERTY_UNKNOWN;
            errorMessage = errorCode.getMessage();
            fieldErrors.add(ErrorResponse.FieldErrorDetail.builder().field(getJsonFieldName(unrecognizedEx.getPath())).reason("처리할 수 없는 필드입니다.").build());
        } else if (cause instanceof MismatchedInputException mismatchedEx) {
            errorCode = GlobalExceptionCodeCluster.Interface.JSON_STRUCTURE_INVALID;
            errorMessage = errorCode.getMessage();
            fieldErrors.add(ErrorResponse.FieldErrorDetail.builder().field(getJsonFieldName(mismatchedEx.getPath())).reason(errorMessage).build());
        }

        Map<String, Object> serverMetadata = Map.of(
                "determinedErrorCode", errorCode.getCode(),
                "debugDetail", debugDetail != null ? debugDetail : "none"
        );

        return buildErrorResponse(errorCode.getCode(), errorMessage, HttpStatus.BAD_REQUEST.value(), fieldErrors, null, serverMetadata, request, e);
    }

    /**
     * 모든 예외 핸들러가 공통으로 사용하는 응답 생성 및 로깅 팩토리 메서드입니다.
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(
            String errorCode,
            String clientMessage,
            int statusCode,
            List<ErrorResponse.FieldErrorDetail> fieldErrors,
            Map<String, Object> clientAttributes,
            Map<String, Object> serverMetadata,
            HttpServletRequest request,
            Exception e
    ) {
        String trackingId = ensureTrackingId();

        // 1. 클라이언트에게 내려보낼 응답 객체 조립
        ErrorResponse response = ErrorResponse.builder()
                .trackingId(trackingId)
                .errorCode(errorCode)
                .message(clientMessage)
                .path(request.getRequestURI())
                .fieldErrors(fieldErrors == null || fieldErrors.isEmpty() ? null : fieldErrors)
                .attributes(clientAttributes)
                .build();

        // 2. 서버 내부 추적을 위한 로깅
        logger.log(request, e, response, serverMetadata);

        // 3. HTTP 상태 코드와 함께 반환
        return ResponseEntity.status(statusCode).body(response);
    }

    private String ensureTrackingId() {
        String traceId = MDC.get(TRACE_ID_KEY);
        if (traceId == null || traceId.isEmpty()) {
            traceId = UUID.randomUUID().toString().substring(0, 8);
            MDC.put(TRACE_ID_KEY, traceId);
        }
        return traceId;
    }

    private String getJsonFieldName(List<JsonMappingException.Reference> path) {
        return path.stream()
                .map(JsonMappingException.Reference::getFieldName)
                .reduce((first, second) -> second)
                .orElse("unknown");
    }

    private String getSimplePropertyName(String propertyPath) {
        if (propertyPath == null) return "unknown";
        int lastDotIndex = propertyPath.lastIndexOf('.');
        return (lastDotIndex != -1) ? propertyPath.substring(lastDotIndex + 1) : propertyPath;
    }
}

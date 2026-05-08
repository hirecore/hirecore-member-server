package io.hirecore.hirecorememberserver.modules.category.application.exception;

import io.hirecore.hirecorememberserver.sharedkernel.application.exception.ApplicationExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

public class CategoryApplicationExceptionCodeCluster {
    @Getter
    @RequiredArgsConstructor
    public enum DetailResponse implements ApplicationExceptionCode {
        JOB_CATEGORY_CODE_NOT_FOUND(
                HttpStatus.NOT_FOUND,
                "요청한 카테고리 코드에 해당하는 활성 직무 카테고리가 존재하지 않습니다.",
                "유효하지 않은 카테고리 코드입니다."
        );

        private final HttpStatus httpStatus;
        private final String logMessage;
        private final String clientMessage;

        @Override
        public String getErrorCode() {
            return this.name();
        }
    }
}

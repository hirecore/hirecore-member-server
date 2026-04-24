package io.hirecore.hirecorememberserver.common.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StrictJsonConfig {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer strictJacksonCustomizer() {
        return builder -> builder.postConfigurer(mapper -> {

            // 1. 스칼라 값(숫자, Boolean)의 자동 변환 방지
            // (주의: Int -> String 변환은 이걸로 안 막아짐)
            mapper.disable(MapperFeature.ALLOW_COERCION_OF_SCALARS);

            // 2. String 필드에 "정수/실수/Boolean"이 오면 무조건 에러 처리 (CoercionConfig)
            mapper.coercionConfigFor(LogicalType.Textual)
                    .setCoercion(CoercionInputShape.Integer, CoercionAction.Fail)
                    .setCoercion(CoercionInputShape.Float, CoercionAction.Fail)
                    .setCoercion(CoercionInputShape.Boolean, CoercionAction.Fail);

            // 3. 정의되지 않은 필드가 오면 에러 (모르는 데이터는 받지 않음)
            // 보안상 DTO에 정의된 필드만 딱 받겠다는 의도
            mapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        });
    }
}

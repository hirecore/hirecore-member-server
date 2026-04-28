package io.hirecore.hirecorememberserver.common.web.converter;

import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.ApiValueEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;

@Component
public class ApiValueEnumConverterFactory implements ConverterFactory<String, ApiValueEnum> {

    @Override
    public <T extends ApiValueEnum> Converter<String, T> getConverter(Class<T> targetType) {

        if (!targetType.isEnum()) {
            throw new IllegalArgumentException("타겟 타입이 Enum이 아닙니다: " + targetType.getName());
        }

        T[] enumConstants = targetType.getEnumConstants();

        return source -> {
            if (source == null || source.isBlank()) return null;

            for (T enumValue : enumConstants) {
                if (enumValue.getApiValue().equalsIgnoreCase(source.trim())) {
                    return enumValue;
                }
            }

            throw new IllegalArgumentException("Unknown Enum Code: " + source);
        };
    }
}
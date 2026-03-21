package io.hirecore.hirecorememberserver.common.mapper;

import org.mapstruct.MapperConfig;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@MapperConfig(
        // 1. Spring Bean으로 자동 등록하여 의존성 주입을 가능하게 합니다.
        componentModel = "spring",

        // 2. 타겟 객체(Target)에 매핑되지 않은 필드가 하나라도 있으면 컴파일 에러를 발생시킵니다. (핵심 안전장치)
        unmappedTargetPolicy = ReportingPolicy.ERROR,

        // 3. 타입 변환 시 데이터 손실이나 문제가 발생할 수 있는 경우 컴파일 에러를 발생시킵니다.
        typeConversionPolicy = ReportingPolicy.ERROR,

        // 4. Source 필드가 null일 경우, Target 필드를 null로 덮어씌우지 않고 무시합니다. (주로 부분 업데이트 시 유용)
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,

        // 5. Source 객체의 필드를 매핑하기 전에 항상 null 체크를 수행하도록 강제합니다. (NPE 방지)
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface GlobalMapStructConfig {
}

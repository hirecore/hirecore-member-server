package io.hirecore.hirecorememberserver.sharedkernel.infrastructure.persistence.jpa;

import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
@Mappings({
    @Mapping(target = "isNew", ignore = true),
    @Mapping(target = "domainEvents", ignore = true)
})
public @interface ToJpaEntityMapping {
}

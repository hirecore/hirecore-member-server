package io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.entity.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.annotations.Comment;

@Embeddable
public record PublicCodeJpaInfo(
        @Comment("사용자 고유 공개 코드(영어 대소문자 및 숫자를 포함한 8글자 제한)")
        @Column(name="public_code", updatable = false, unique = true, nullable = false, columnDefinition = "VARCHAR(8)")
        String publicCode
) {
}

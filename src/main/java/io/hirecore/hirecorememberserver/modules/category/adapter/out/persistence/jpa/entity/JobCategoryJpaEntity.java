package io.hirecore.hirecorememberserver.modules.category.adapter.out.persistence.jpa.entity;

import io.hirecore.hirecorememberserver.sharedkernel.infrastructure.persistence.jpa.AbstractPersistableAggregateRoot;
import io.hirecore.hirecorememberserver.sharedkernel.infrastructure.persistence.jpa.AuditingJpaInfo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "job_category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class JobCategoryJpaEntity extends AbstractPersistableAggregateRoot<Long> {
    @Id
    private Long id;

    @Comment("상위 카테고리 ID")
    @Column(name = "parent_id")
    private Long parentId;

    @Comment("직무 카테고리 코드")
    @Column(name = "category_code", nullable = false, columnDefinition = "VARCHAR(30)")
    private String categoryCode;

    @Comment("카테고리 이름")
    @Column(name = "category_name", nullable = false, columnDefinition = "VARCHAR(30)")
    private String categoryName;

    @Comment("카테고리 깊이")
    @Column(name = "depth", nullable = false)
    private Integer depth;

    @Comment("활성 여부")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Comment("직접 매핑 가능 여부")
    @Column(name = "is_assignable", nullable = false)
    private Boolean isAssignable;

    @Comment("사용자 정의 입력 허용 여부")
    @Column(name = "allows_custom_input", nullable = false)
    private Boolean allowsCustomInput;

    @Comment("정렬 순서")
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Embedded
    private AuditingJpaInfo auditingInfo;
}

package io.hirecore.hirecorememberserver.modules.category.adapter.out.persistence.jpa.entity;

import io.hirecore.hirecorememberserver.common.adapter.out.persistence.jpa.AbstractPersistableAggregateRoot;
import io.hirecore.hirecorememberserver.common.adapter.out.persistence.jpa.vo.AuditingJpaInfo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "job_categories")
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
    @Column(name = "job_category_code", nullable = false, columnDefinition = "VARCHAR(30)")
    private String jobCategoryCode;

    @Comment("카테고리 이름")
    @Column(name = "category_name", nullable = false, columnDefinition = "VARCHAR(30)")
    private String categoryName;

    @Comment("카테고리 깊이")
    @Column(name = "depth", nullable = false)
    private Integer depth;

    @Comment("활성 여부")
    @Column(name = "is_activity", nullable = false)
    private Boolean isActivity;

    @Comment("사용자 정의 입력 허용 여부")
    @Column(name = "allows_custom_input", nullable = false)
    private Boolean allowsCustomInput;

    @Comment("정렬 순서")
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Embedded
    private AuditingJpaInfo auditingInfo;
}

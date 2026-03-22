package io.hirecore.hirecorememberserver.modules.policy.adapter.out.persistence.jpa.entity;

import io.hirecore.hirecorememberserver.common.adapter.out.persistence.jpa.AbstractPersistableAggregateRoot;
import io.hirecore.hirecorememberserver.common.adapter.out.persistence.jpa.vo.AuditingJpaInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "user_membership_policies")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@SuperBuilder
@Getter
public class UserMembershipPolicyJpaEntity extends AbstractPersistableAggregateRoot<Long> {
    @Id
    private Long id;

    @Comment("멤버십 이름")
    @Column(name = "name", nullable = false)
    private String name;

    @Comment("멤버십 설명")
    @Column(name = "content", nullable = false)
    private String content;

    @Comment("멤버십 금액")
    @Column(name = "amount", nullable = false)
    private Long amount;

    @Comment("스토리지 할당량 (바이트)")
    @Column(name = "storage_quota_byte", nullable = false)
    private Long storageQuotaByte;

    @Comment("활성화 여부")
    @Column(name = "active", nullable = false)
    private Boolean active;

    @Embedded
    private AuditingJpaInfo auditingInfo;
}

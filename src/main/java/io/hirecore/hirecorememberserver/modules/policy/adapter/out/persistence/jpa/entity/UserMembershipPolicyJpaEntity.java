package io.hirecore.hirecorememberserver.modules.policy.adapter.out.persistence.jpa.entity;

import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.AbstractPersistableAggregateRoot;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.AuditingJpaInfo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "user_membership_policies")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Builder
@Getter
public class UserMembershipPolicyJpaEntity extends AbstractPersistableAggregateRoot<Long> {
    @Id
    private Long id;

    @Comment("멤버십 고유 식별 코드")
    @Column(name = "code", nullable = false)
    private String code;

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
    @Column(name = "storage_quota_bytes", nullable = false)
    private Long storageQuotaBytes;

    @Comment("활성화 여부")
    @Column(name = "active", nullable = false)
    private Boolean active;

    @Embedded
    private AuditingJpaInfo auditingInfo;
}

package io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.entity;

import io.hirecore.hirecorememberserver.common.adapter.out.persistence.jpa.AbstractPersistableEntity;
import io.hirecore.hirecorememberserver.common.adapter.out.persistence.jpa.vo.AuditingJpaInfo;
import io.hirecore.hirecorememberserver.modules.storage.domain.vo.ResourceKind;
import io.hirecore.hirecorememberserver.modules.storage.domain.vo.UsageChangeReason;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "user_storage_usage_logs")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Builder
@Getter
public class UserStorageUsageLogJpaEntity extends AbstractPersistableEntity<Long> {
    @Id
    private Long id;

    @Comment("회원 계정 ID")
    @Column(name = "member_account_id", nullable = false)
    private Long memberAccountId;

    @Comment("사용량 변경 사유")
    @Enumerated(EnumType.STRING)
    @Column(name = "usage_change_reason", nullable = false, columnDefinition = "VARCHAR(30)")
    private UsageChangeReason usageChangeReason;

    @Comment("변경 대상 리소스 종류")
    @Enumerated(EnumType.STRING)
    @Column(name = "resource_kind", nullable = false, columnDefinition = "VARCHAR(30)")
    private ResourceKind resourceKind;

    @Comment("변경 대상 리소스 식별자")
    @Column(name = "resource_kind_id", nullable = false)
    private Long resourceKindId;

    @Comment("변경 용량 (바이트, 양수=증가/음수=감소)")
    @Column(name = "change_byte", nullable = false)
    private Long changeByte;

    @Comment("변경 전 총 사용량 (바이트)")
    @Column(name = "before_used_quota_byte", nullable = false)
    private Long beforeUsedQuotaByte;

    @Comment("변경 후 총 사용량 (바이트)")
    @Column(name = "after_used_quota_byte", nullable = false)
    private Long afterUsedQuotaByte;

    @Comment("멱등 키 (중복 처리 방지)")
    @Column(name = "idempotency_key", nullable = false, unique = true)
    private String idempotencyKey;

    @Embedded
    private AuditingJpaInfo auditingInfo;
}

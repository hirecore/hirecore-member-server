package io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.entity;

import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.AbstractPersistableAggregateRoot;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.AuditingJpaInfo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.Instant;

@Entity
@Table(name = "user_storage_usages")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Builder
@Getter
public class UserStorageUsageJpaEntity extends AbstractPersistableAggregateRoot<Long> {
    @Id
    private Long id;

    @Version
    private Long version;

    @Comment("회원 계정 ID")
    @Column(name = "member_account_id", nullable = false, unique = true)
    private Long memberAccountId;

    @Comment("현재 사용 중인 스토리지 용량 (바이트)")
    @Column(name = "used_quota_bytes", nullable = false)
    private Long usedQuotaBytes;

    @Embedded
    private AuditingJpaInfo auditingInfo;

    public void applyUsedQuotaBytes(Long usedQuotaBytes, Instant updatedAt) {
        this.usedQuotaBytes = usedQuotaBytes;
        this.auditingInfo = new AuditingJpaInfo(this.auditingInfo.createdAt(), updatedAt);
    }
}

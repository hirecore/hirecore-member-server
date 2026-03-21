package io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.entity;

import io.hirecore.hirecorememberserver.common.adapter.out.persistence.jpa.AbstractPersistableAggregateRoot;
import io.hirecore.hirecorememberserver.common.adapter.out.persistence.jpa.vo.AuditingJpaInfo;
import io.hirecore.hirecorememberserver.modules.account.domain.vo.MemberRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

/**
 * <h3>Aggregate Root</h3>
 */
@Entity
@Table(name="member_accounts")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@SuperBuilder
@Getter
public class MemberAccountJpaEntity extends AbstractPersistableAggregateRoot<Long> {
    @Id
    private Long id;

    @Comment("회원 이메일(로그인 아이디)")
    @Column(updatable = false, unique = true, nullable = false)
    private String email;

    @Comment("회원 패스워드")
    @Column(name="password")
    private String password;

    @Comment("회원 권한")
    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Embedded
    private AuditingJpaInfo auditingInfo;
}

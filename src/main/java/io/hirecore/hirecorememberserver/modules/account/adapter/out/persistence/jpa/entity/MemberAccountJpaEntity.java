package io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.entity;

import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.AbstractPersistableAggregateRoot;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.AuditingJpaInfo;
import io.hirecore.hirecorememberserver.modules.account.domain.vo.MemberRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.Comment;

// Aggregate Root
@Entity
@Table(name="member_accounts")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Builder
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

    @Comment("토큰 버전 (로그아웃 시 증가하여 기존 토큰을 무효화)")
    @Column(name = "token_version", nullable = false)
    private Integer tokenVersion;

    @Embedded
    private AuditingJpaInfo auditingInfo;
}

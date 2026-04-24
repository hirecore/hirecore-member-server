package io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.entity;

import io.hirecore.hirecorememberserver.sharedkernel.infrastructure.persistence.jpa.AbstractPersistableAggregateRoot;
import io.hirecore.hirecorememberserver.sharedkernel.infrastructure.persistence.jpa.AuditingJpaInfo;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.OAuth2Provider;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.Instant;

@Entity
@Table(name="social_accounts")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Builder
@Getter
public class SocialAccountJpaEntity extends AbstractPersistableAggregateRoot<Long> {
    @Id
    private Long id;

    @Comment("연동된 회원 계정 ID")
    @Column(name="member_account_id", nullable = false)
    private Long memberAccountId;

    @Comment("소셜 로그인 제공자")
    @Enumerated(EnumType.STRING)
    @Column(name="provider", nullable = false, columnDefinition = "VARCHAR(20)")
    private OAuth2Provider provider;

    @Comment("소셜 사용자 식별 아이디")
    @Column(name="provider_id", updatable = false, nullable = false)
    private String providerId;

    @Comment("소셜 계정 이메일")
    @Column(name="email", nullable = false)
    private String email;

    @Comment("소셜 로그인 서비스 연동 일자")
    @Column(name = "connected_at")
    private Instant connectedAt;

    @Comment("이메일 동의여부")
    @Column(name="email_agreed", nullable = false)
    private Boolean emailAgreed;

    @Comment("프로필 닉네임 동의여부")
    @Column(name="profile_nickname_agreed", nullable = false)
    private Boolean profileNicknameAgreed;

    @Embedded
    private AuditingJpaInfo auditingInfo;
}

package io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.entity;

import io.hirecore.hirecorememberserver.common.adapter.out.persistence.jpa.AbstractPersistableAggregateRoot;
import io.hirecore.hirecorememberserver.common.adapter.out.persistence.jpa.vo.AuditingJpaInfo;
import io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.entity.vo.ProfileImageJpaInfo;
import io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.entity.vo.PublicCodeJpaInfo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@Table(
        name = "profiles",
        uniqueConstraints = @UniqueConstraint(columnNames = {
                "member_account_id", "publicCode"
        })
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class ProfileJpaEntity extends AbstractPersistableAggregateRoot<Long> {
    @Id
    private Long id;

    @Comment("회원 계정 ID")
    @Column(name="member_account_id", updatable = false, unique = true, nullable = false)
    private Long memberAccountId;

    @OneToOne(
            mappedBy = "profile",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private ProfileDetailJpaEntity profileDetail;

    @Embedded
    private PublicCodeJpaInfo publicCodeInfo;

    @Comment("닉네임")
    @Column(name="nickname", nullable = false, columnDefinition = "VARCHAR(50)")
    private String nickname;

    @Embedded
    private ProfileImageJpaInfo profileImageInfo;

    @Comment("전화번호")
    @Column(name="phone_number", columnDefinition = "VARCHAR(30)")
    private String phoneNumber;

    @Embedded
    private AuditingJpaInfo auditingInfo;


    public void syncProfileDetail(ProfileDetailJpaEntity profileDetail) {
        this.profileDetail = profileDetail;
        if (profileDetail != null) {
            profileDetail.setProfile(this);
        }
    }
}

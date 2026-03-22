package io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "profile_details")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "profile_detail_type")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ProfileDetailJpaEntity {
    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private ProfileJpaEntity profile;

    protected void setProfile(ProfileJpaEntity profile) {
        this.profile = profile;
    }
}

package io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_profile_details")
@DiscriminatorValue("USER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfileDetailJpaEntity extends ProfileDetailJpaEntity {
    @Column(name = "marketing_email")
    private String marketingEmail;

    @Builder
    private UserProfileDetailJpaEntity(String marketingEmail) {
        this.marketingEmail = marketingEmail;
    }
}

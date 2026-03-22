package io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.entity.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.annotations.Comment;

@Embeddable
public record ProfileImageJpaInfo(
        @Comment("원본 프로필 이미지 이름")
        @Column(name="origin_profile_image_name")
        String originProfileImageName,

        @Comment("저장소 프로필 이미지 이름")
        @Column(name="storage_profile_image_name")
        String storageProfileImageName,

        @Comment("저장소 프로필 이미지 경로")
        @Column(name="storage_profile_image_path")
        String storageProfileImagePath
) {
}

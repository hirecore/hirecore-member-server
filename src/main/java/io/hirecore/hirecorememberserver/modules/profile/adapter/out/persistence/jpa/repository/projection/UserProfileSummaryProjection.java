package io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.repository.projection;

public interface UserProfileSummaryProjection {
    String getPublicCode();
    String getNickname();
    String getStorageProfileImagePath();
}

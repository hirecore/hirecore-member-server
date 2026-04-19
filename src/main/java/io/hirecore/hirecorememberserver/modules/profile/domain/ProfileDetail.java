package io.hirecore.hirecorememberserver.modules.profile.domain;

public sealed interface ProfileDetail permits UserProfileDetail {
    Long getProfileId();
}

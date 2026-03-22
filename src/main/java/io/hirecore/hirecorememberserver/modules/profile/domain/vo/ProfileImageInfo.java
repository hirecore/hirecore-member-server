package io.hirecore.hirecorememberserver.modules.profile.domain.vo;

public record ProfileImageInfo (
        String originProfileImageName,
        String storageProfileImageName,
        String storageProfileImagePath
){
    public static ProfileImageInfo init() {
        return new ProfileImageInfo(null, null, null);
    }
}

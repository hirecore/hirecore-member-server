package io.hirecore.hirecorememberserver.modules.profile.application.port.out;

public interface ProfileQueryPort {
    boolean existsByPublicCode(String code);
}

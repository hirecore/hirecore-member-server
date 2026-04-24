package io.hirecore.hirecorememberserver.modules.profile.application.port.out;

public interface CheckProfilePublicCodePort {
    boolean existsByPublicCode(String code);
}

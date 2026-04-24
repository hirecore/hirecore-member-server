package io.hirecore.hirecorememberserver.sharedkernel.application.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public record AuthPrincipal(
        Long id,
        String email,
        String role,
        int tokenVersion
) {
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String authority = "ROLE_" + role;
        return Collections.singletonList(new SimpleGrantedAuthority(authority));
    }
}

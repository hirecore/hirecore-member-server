package io.hirecore.hirecorememberserver.common.security;

import io.hirecore.hirecorememberserver.common.web.logging.StructuredErrorLogger;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.ResolveTokenSharedPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.ValidateTokenVersionSharedPort;
import jakarta.servlet.http.HttpServletResponse;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
public class WebMvcSecuritySupport {
    @Bean
    public ResolveTokenSharedPort tokenResolverPort() {
        return Mockito.mock(ResolveTokenSharedPort.class);
    }

    @Bean
    public ValidateTokenVersionSharedPort tokenVersionValidationPort() {
        return Mockito.mock(ValidateTokenVersionSharedPort.class);
    }

    @Bean
    public StructuredErrorLogger structuredErrorLogger() {
        return Mockito.mock(StructuredErrorLogger.class);
    }

    @Bean
    public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                        )
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login/**").permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}

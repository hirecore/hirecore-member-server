package io.hirecore.hirecorememberserver.common.adapter.in;

import io.hirecore.hirecorememberserver.common.adapter.in.logging.StructuredErrorLogger;
import io.hirecore.hirecorememberserver.common.application.port.out.TokenResolverPort;
import io.hirecore.hirecorememberserver.common.application.port.out.TokenVersionValidationPort;
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
    public TokenResolverPort tokenResolverPort() {
        return Mockito.mock(TokenResolverPort.class);
    }

    @Bean
    public TokenVersionValidationPort tokenVersionValidationPort() {
        return Mockito.mock(TokenVersionValidationPort.class);
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

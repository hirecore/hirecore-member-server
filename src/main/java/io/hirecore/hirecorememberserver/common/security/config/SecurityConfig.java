package io.hirecore.hirecorememberserver.common.security.config;

import io.hirecore.hirecorememberserver.common.security.handler.ApiAccessDeniedHandler;
import io.hirecore.hirecorememberserver.common.security.handler.ApiAuthenticationEntryPoint;
import io.hirecore.hirecorememberserver.common.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfigurationSource userWebConfig;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ApiAuthenticationEntryPoint apiAuthenticationEntryPoint;
    private final ApiAccessDeniedHandler apiAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors((cors) ->
                        cors.configurationSource(userWebConfig)
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        // 세션 설정 (Stateless: 서버가 세션을 기억하지 않음 -> JWT 필수)
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(apiAuthenticationEntryPoint)
                        .accessDeniedHandler(apiAccessDeniedHandler)
                )
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/docs/**").permitAll()
                                .requestMatchers("/api/auth/login/**").permitAll()
                                .requestMatchers("/api/categories/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/portfolios/summaries/mine").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/portfolios/{portfolioId}").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/portfolios/{portfolioId}/edit").authenticated()
                                .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

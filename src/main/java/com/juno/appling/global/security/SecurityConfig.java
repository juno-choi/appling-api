package com.juno.appling.global.security;

import com.juno.appling.member.enums.Role;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final CustomEntryPoint entryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    private static final String[] SELLER_LIST = {
        "/api/seller/**"
    };

    private static final String[] MEMBER_LIST = {
        "/api/member/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(c -> c.disable())
            .cors(c -> c.configurationSource(new CorsConfigurationSource() {
                @Override
                public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOriginPatterns(Collections.singletonList("*"));
                    config.setAllowedMethods(Collections.singletonList("*"));
                    config.setAllowCredentials(true);
                    config.setAllowedHeaders(Collections.singletonList("*"));
                    config.setMaxAge(600L);
                    return config;
                }
            }))
            .headers(c -> c.frameOptions(f -> f.disable()).disable())
            .authorizeHttpRequests(auth ->
                auth
                    .requestMatchers(PathRequest.toH2Console()).permitAll()
                    .requestMatchers(SELLER_LIST).hasRole(Role.SELLER.roleName)
                    .requestMatchers(MEMBER_LIST).hasRole(Role.MEMBER.roleName)
                    .anyRequest().permitAll()
            ).exceptionHandling(c ->
                c.authenticationEntryPoint(entryPoint).accessDeniedHandler(accessDeniedHandler)
            ).sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .apply(new JwtSecurityConfig(tokenProvider))
        ;
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

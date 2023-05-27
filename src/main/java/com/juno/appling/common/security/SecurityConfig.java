package com.juno.appling.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] WHITE_LIST = {
            "/**",
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.csrf().disable()
                .cors().disable()
                .headers().frameOptions().disable().disable()
                .authorizeHttpRequests(auth -> {
                    try{
                        auth.requestMatchers(WHITE_LIST).permitAll()
                        ;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                })
        ;
        return http.build();
    }
}

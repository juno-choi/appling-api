package com.juno.appling.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final AuthService authService;
    private final ObjectPostProcessor<Object> objectPostProcessor;

    private static final String[] WHITE_LIST = {
            "/**", "/member/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf(c -> c.disable())
                .cors(c -> c.disable())
                .headers(c -> c.frameOptions(f -> f.disable()).disable())
                .authorizeHttpRequests(auth -> {
                    try{
                        auth
                                .requestMatchers(WHITE_LIST).permitAll()
                                .anyRequest().authenticated()
                        ;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }).addFilterBefore(loginFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(c ->
                        c.authenticationEntryPoint(null).accessDeniedHandler(null)
                )
        ;
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    private LoginFilter loginFilter() throws Exception {
        LoginFilter filter = new LoginFilter();
        filter.setAuthenticationManager(authenticationManager(new AuthenticationManagerBuilder(objectPostProcessor)));
        filter.setFilterProcessesUrl("/auth/login");
        return filter;
    }

    public AuthenticationManager authenticationManager(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(authService).passwordEncoder(passwordEncoder());
        return auth.build();
    }
}

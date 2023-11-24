package com.example.demojwt.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.demojwt.jwt.jwtAutheticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration //indica clase de configuracion
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final jwtAutheticationFilter jwtAutheticationFilter;
    private final AuthenticationProvider authProvider;

    @Bean
    public SecurityFilterChain securityFiltrerChain(HttpSecurity http) throws Exception
    {
        return http
            .csrf(csrf ->
            csrf
            .disable())
            .authorizeHttpRequests(authRequest -> 
            authRequest
                .requestMatchers("/auth/**").permitAll()
                .anyRequest().authenticated()
                )
                .sessionManagement(sessionManagement ->
                sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authProvider)
                .addFilterBefore(jwtAutheticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}

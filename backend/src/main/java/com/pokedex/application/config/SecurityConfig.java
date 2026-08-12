package com.pokedex.application.config;

import tools.jackson.databind.ObjectMapper;
import com.pokedex.infrastructure.adapter.in.ErrorResponse;
import com.pokedex.infrastructure.adapter.out.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration: stateless JWT, read routes public and write routes protected. Missing or
 * invalid tokens yield 401; forbidden access yields 403 — both returned as the standard
 * {@link ErrorResponse} body (no infrastructure detail leaks).
 */
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final ObjectMapper objectMapper;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter, ObjectMapper objectMapper) {
        this.jwtFilter = jwtFilter;
        this.objectMapper = objectMapper;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public: auth endpoints and read routes
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/pokemon/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        // Protected: sync + write routes
                        .requestMatchers(HttpMethod.POST, "/api/pokemon/sync").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/pokemon/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/pokemon/**").authenticated()
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler()))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    private AuthenticationEntryPoint authEntryPoint() {
        return (request, response, authException) ->
                writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized",
                        "Authentication required", request.getRequestURI());
    }

    private AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) ->
                writeError(response, HttpServletResponse.SC_FORBIDDEN, "Forbidden",
                        "Access denied", request.getRequestURI());
    }

    private void writeError(HttpServletResponse response, int status, String error, String message, String path)
            throws java.io.IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse body = ErrorResponse.of(status, error, message, path);
        objectMapper.writeValue(response.getWriter(), body);
    }
}

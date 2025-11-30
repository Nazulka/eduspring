package com.lms.eduspring.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.context.SecurityContextHolder;

@TestConfiguration
@EnableMethodSecurity(
        prePostEnabled = false,   // disable @PreAuthorize / @PostAuthorize
        securedEnabled = false,   // disable @Secured
        jsr250Enabled = false     // ❗ disable @RolesAllowed / @PermitAll
)
public class TestSecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager() {
        return authentication -> {
            authentication.setAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return authentication;
        };
    }
}

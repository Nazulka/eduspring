package com.lms.eduspring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig: Configures Spring Security to allow public access to certain paths.
 *
 * This configuration overrides the default "deny all" behavior.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Defines the security filter chain which contains all authorization rules.
     *
     * @param http The HttpSecurity object to configure.
     * @return The configured SecurityFilterChain bean.
     * @throws Exception if configuration fails.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Configure authorization rules
                .authorizeHttpRequests(requests -> requests
                        // Allow public access to all static front-end pages, API auth endpoints,
                        // and the H2 console.
                        .requestMatchers(
                                "/",
                                "/login",
                                "/register",
                                "/api/auth/**", // For public registration/login API calls
                                "/h2-console/**" // For the H2 database console UI
                        ).permitAll()
                        // Require authentication for all other requests
                        .anyRequest().authenticated()
                )

                // 2. Configure header options, necessary for H2 console to work in a browser
                // by disabling X-Frame-Options protection for the console's iframe.
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))

                // 3. Configure form login mechanism
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                )
                // 4. Configure logout mechanism
                .logout(LogoutConfigurer::permitAll
                )
                // 5. Disable CSRF protection for /h2-console/ and Postman/cURL testing
                // NOTE: You should keep CSRF enabled for production front-end forms.
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}

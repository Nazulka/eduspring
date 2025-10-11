package com.lms.eduspring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Import the specific implementation
import org.springframework.security.crypto.password.PasswordEncoder; // Import the interface
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig: Configures Spring Security to allow public access to certain paths
 * and provides necessary security beans like the PasswordEncoder.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Define the PasswordEncoder bean.
     * This method tells Spring that when any component (like UserService)
     * asks for a PasswordEncoder, it should inject a BCryptPasswordEncoder instance.
     * @return BCryptPasswordEncoder instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt is the standard, highly secure password hashing function.
        return new BCryptPasswordEncoder();
    }


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
                                "/h2-console/**", // For the H2 database console UI
                                "/css/**" // Important: allow access to static resources like CSS
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
                .logout(logout -> logout
                        .permitAll()
                )
                // 5. Disable CSRF protection for /h2-console/ and Postman/cURL testing
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}

package co.parameta.technical.test.rest.configuration;

import co.parameta.technical.test.commons.util.exception.CustomAccessDeniedHandler;
import co.parameta.technical.test.commons.util.exception.CustomAuthenticationEntryPoint;
import co.parameta.technical.test.commons.util.helper.JWTAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration for the REST API.
 * <p>
 * This configuration secures the application using JWT-based authentication
 * and enforces stateless session management.
 * </p>
 *
 * <p>
 * It also defines custom handlers for authentication and authorization errors,
 * ensuring consistent and meaningful security responses.
 * </p>
 *
 * <ul>
 *     <li>JWT authentication filter</li>
 *     <li>Stateless session policy</li>
 *     <li>Custom authentication entry point</li>
 *     <li>Custom access denied handler</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {

    private final JWTAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final AuthenticationProvider authenticationProvider;

    /**
     * Configures the {@link SecurityFilterChain} for HTTP security.
     * <p>
     * Public endpoints are explicitly permitted (login and Swagger resources),
     * while all other endpoints require authentication.
     * </p>
     *
     * <p>
     * CSRF protection is disabled because the API is stateless and uses JWT.
     * </p>
     *
     * @param http {@link HttpSecurity} to configure
     * @return configured {@link SecurityFilterChain}
     * @throws Exception if a security configuration error occurs
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(
                                        "/login/**",
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/v3/api-docs.yaml"
                                ).permitAll()
                                .anyRequest().authenticated()
                )
                .sessionManagement(sessionManager ->
                        sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(handling ->
                        handling
                                .authenticationEntryPoint(customAuthenticationEntryPoint)
                                .accessDeniedHandler(customAccessDeniedHandler)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}

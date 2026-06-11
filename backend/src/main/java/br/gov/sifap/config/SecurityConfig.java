package br.gov.sifap.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Security configuration (ADR-005).
 *
 * <p>Stateless OAuth2/JWT resource server with <b>deny-by-default</b>
 * authorization. The OpenAPI UI is public; every {@code /api/**} endpoint
 * requires authentication. Legacy backdoors (região 99, CPF prefixes) are NOT
 * granted here — they are modelled as explicit, audited business exceptions
 * under a dedicated role once OQ-006/007/008 are ratified.
 */
@Configuration
public class SecurityConfig {

    private final String issuerUri;
    private final List<String> allowedOrigins;

    public SecurityConfig(
            @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:}") String issuerUri,
            @Value("${sifap.cors.allowed-origins:http://localhost:3000}") List<String> allowedOrigins) {
        this.issuerUri = issuerUri;
        this.allowedOrigins = allowedOrigins;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/actuator/health")
                        .permitAll()
                        .anyRequest().authenticated());

        // Only wire the JWT resource server when an issuer is configured.
        // Web-layer tests use @WithMockUser and do not need a decoder.
        if (StringUtils.hasText(issuerUri)) {
            http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        }

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}

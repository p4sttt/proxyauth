package me.d4y2k.apigateway.config;

import me.d4y2k.apigateway.filter.AuthPostProcessingFilter;
import me.d4y2k.apigateway.jwt.CustomJwtAuthoritiesConverter;
import me.d4y2k.apigateway.filter.TokenRelayFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Security configuration class for setting up WebFlux security filters and OAuth2 resource server.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * Configures the security filter chain.
     *
     * @param http the ServerHttpSecurity instance
     * @return the configured SecurityWebFilterChain
     */
    @Bean
    SecurityWebFilterChain baseSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                // move token from cookie to header
                .addFilterBefore(new TokenRelayFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                // if success authentication, redirect to original request, else redirect to login page
                .addFilterAfter(new AuthPostProcessingFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/public/**").permitAll()
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(new CustomJwtAuthoritiesConverter())));
        return http.build();
    }

}

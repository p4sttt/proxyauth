package me.d4y2k.apigateway.jwt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

/**
 * Configuration class for setting up JWT security.
 */
@Configuration
public class JwtSecurityConfig {

    /**
     * Creates a bean for the ReactiveJwtDecoder.
     *
     * @return a ReactiveJwtDecoder instance
     */
    @Bean
    ReactiveJwtDecoder reactiveJwtDecoder() {
        return new CustomJwtDecoder();
    }

}

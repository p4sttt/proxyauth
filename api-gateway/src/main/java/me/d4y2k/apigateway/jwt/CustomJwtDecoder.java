package me.d4y2k.apigateway.jwt;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

public class CustomJwtDecoder implements ReactiveJwtDecoder {

    private static final Jwt TEST_JWT = new Jwt(
            "a.a.a",
            Instant.now(),
            Instant.now().plusSeconds(60 * 60),
            Map.of("alg", "HS256"),
            Map.of(
                    "sub", "test-subject",
                    "scope", "test-scope",
                    "roles", Collections.singletonList("user"))
    );

    @Override
    public Mono<Jwt> decode(String token) throws JwtException {
        return Mono.just(TEST_JWT);
    }

}

package me.d4y2k.apigateway.jwt;

import io.micrometer.common.lang.NonNullApi;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A converter that extracts authorities from a JWT and creates an authentication token.
 */
@Component
@NonNullApi
public class CustomJwtAuthoritiesConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    public static final String ROLES_CLAIM = "roles";

    /**
     * Converts a JWT into an authentication token with granted authorities.
     *
     * @param jwt the JWT to convert
     * @return a Mono emitting the authentication token
     */
    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        List<GrantedAuthority> authorities = extractAuthorities(jwt);
        return Mono.just(new JwtAuthenticationToken(jwt, authorities));
    }

    /**
     * Extracts granted authorities from the roles claim in the JWT.
     *
     * @param jwt the JWT from which to extract authorities
     * @return a list of granted authorities
     */
    private List<GrantedAuthority> extractAuthorities(Jwt jwt) {
        List<String> roles = jwt.getClaimAsString(ROLES_CLAIM) == null
                ? List.of()
                : jwt.getClaimAsStringList(ROLES_CLAIM);

        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

}

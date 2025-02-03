package me.d4y2k.apigateway.filter;

import io.micrometer.common.lang.NonNullApi;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Filter that moves the token from a cookie to the Authorization header.
 */
@Component
@NonNullApi
public class TokenRelayFilter implements WebFilter {

    private static final String TOKEN_COOKIE_KEY = "token";
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * Filters the request to relay the token from the cookie to the Authorization header.
     *
     * @param exchange the current server exchange
     * @param chain the web filter chain
     * @return a Mono that indicates when request processing is complete
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (request.getCookies().containsKey(TOKEN_COOKIE_KEY)) {
            HttpCookie tokenCookie = request.getCookies().getFirst(TOKEN_COOKIE_KEY);

            if (tokenCookie == null) {
                return chain.filter(exchange);
            }

            String token = tokenCookie.getValue();
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        }

        return chain.filter(exchange);
    }
}

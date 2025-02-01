package me.d4y2k.apigateway;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class TokenRelayFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (request.getCookies().containsKey("token")) {
            HttpCookie tokenCookie = request.getCookies().getFirst("token");

            if (tokenCookie == null) {
                return chain.filter(exchange);
            }

            String token = tokenCookie.getValue();
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }

}

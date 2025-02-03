package me.d4y2k.apigateway.filter;

import io.micrometer.common.lang.NonNullApi;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A filter that limits the rate of API requests.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@NonNullApi
public class RateLimitFilter implements WebFilter {

    private static final int MAX_REQUESTS_PER_MINUTE = 60;
    private final ConcurrentHashMap<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String clientIp = null;
        if (exchange.getRequest().getRemoteAddress() != null) {
            clientIp = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        }

        if (clientIp == null) {
            return chain.filter(exchange);
        }

        AtomicInteger requestCount = requestCounts.computeIfAbsent(clientIp, k -> new AtomicInteger(0));

        if (requestCount.incrementAndGet() > MAX_REQUESTS_PER_MINUTE) {
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange)
                .publishOn(Schedulers.boundedElastic())
                .doFinally(signalType -> Mono.delay(Duration.ofMinutes(1))
                        .doOnTerminate(requestCount::decrementAndGet)
                        .subscribe());
    }
}
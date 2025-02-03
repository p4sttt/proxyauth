package me.d4y2k.apigateway.filter;

import io.micrometer.common.lang.NonNullApi;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * A filter that processes authentication results and redirects the user based on the authentication status.
 */
@Component
@NonNullApi
public class AuthPostProcessingFilter implements WebFilter {


    public static final String X_ORIGINAL_HOST = "X-Original-Host";
    public static final String X_ORIGINAL_PORT = "X-Original-Port";
    public static final String X_ORIGINAL_PATH = "X-Original-Path";

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(AuthPostProcessingFilter.class);

    /**
     * Filters the request based on the authentication status.
     *
     * @param exchange the current server exchange
     * @param chain the web filter chain
     * @return a Mono that indicates when request processing is complete
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(context -> {
                    if (isAuthenticated(context))
                        return processSuccessAuthentication(exchange);
                    else
                        return processFailedAuthentication(exchange);
                });
    }

    /**
     * Checks if the user is authenticated.
     *
     * @param context the security context
     * @return true if the user is authenticated, false otherwise
     */
    private boolean isAuthenticated(SecurityContext context) {
        return context.getAuthentication() != null && context.getAuthentication().isAuthenticated();
    }

    /**
     * Creates a location URI string from the given host, port, and path.
     *
     * @param host the original host
     * @param port the original port
     * @param uri the original path
     * @return the constructed location URI string
     */
    private String createLocation(String host, String port, String uri) {
        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(host)
                .port(port)
                .path(uri)
                .build()
                .toUriString();
    }

    /**
     * Processes a successful authentication by redirecting the user to the original request URI.
     *
     * @param exchange the current server exchange
     * @return a Mono that indicates when the redirection is complete
     */
    private Mono<Void> processSuccessAuthentication(ServerWebExchange exchange) {
        var request = exchange.getRequest();
        var response = exchange.getResponse();

        String originalHost = request.getHeaders().getFirst(X_ORIGINAL_HOST);
        String originalPort = request.getHeaders().getFirst(X_ORIGINAL_PORT);
        String originalPath = request.getHeaders().getFirst(X_ORIGINAL_PATH);

        if (originalHost == null || originalPort == null || originalPath == null) {
            return Mono.error(new IllegalArgumentException("X-Original-Base-URL or X-Original-URI header is missing"));
        }

        var location = createLocation(originalHost, originalPort, originalPath);
        response.setStatusCode(HttpStatus.FOUND);
        response.getHeaders().setLocation(URI.create(location));
        log.info("Redirecting to: {}", location);

        return exchange.getResponse().setComplete();
    }

    /**
     * Processes a failed authentication by setting the response status to UNAUTHORIZED.
     *
     * @param exchange the current server exchange
     * @return a Mono that indicates when the response is complete
     */
    private Mono<Void> processFailedAuthentication(ServerWebExchange exchange) {
        // TODO: implements redirect to login page
        var response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}

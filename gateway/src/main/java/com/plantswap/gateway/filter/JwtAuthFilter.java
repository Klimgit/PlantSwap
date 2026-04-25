package com.plantswap.gateway.filter;

import com.plantswap.gateway.config.PublicPathsProperties;
import com.plantswap.gateway.security.JwtValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

/**
 * Глобальный JWT-фильтр API Gateway.
 */
@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private static final String HEADER_AUTHORIZATION = HttpHeaders.AUTHORIZATION;
    private static final String BEARER_PREFIX        = "Bearer ";
    private static final String HEADER_USER_ID       = "X-User-Id";
    private static final String HEADER_REQUEST_ID    = "X-Request-Id";

    private final JwtValidator jwtValidator;
    private final List<String> publicPaths;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public JwtAuthFilter(JwtValidator jwtValidator, PublicPathsProperties publicPathsProperties) {
        this.jwtValidator = jwtValidator;
        this.publicPaths = publicPathsProperties.publicPaths();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        String requestId = request.getHeaders().getFirst(HEADER_REQUEST_ID);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        final String finalRequestId = requestId;

        if (isPublicPath(path)) {
            log.debug("Публичный маршрут, JWT не требуется: {} {}", request.getMethod(), path);
            return chain.filter(addRequestId(exchange, finalRequestId));
        }

        String authHeader = request.getHeaders().getFirst(HEADER_AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            log.warn("Отсутствует Authorization заголовок: {} {}", request.getMethod(), path);
            return unauthorized(exchange.getResponse(), "Требуется авторизация");
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        return jwtValidator.extractUserId(token)
                .map(userId -> {
                    log.debug("JWT валиден: userId={}, path={}", userId, path);

                    ServerWebExchange mutated = exchange.mutate()
                            .request(r -> r
                                    .header(HEADER_USER_ID, userId)
                                    .header(HEADER_REQUEST_ID, finalRequestId))
                            .build();

                    mutated.getResponse().getHeaders().add(HEADER_REQUEST_ID, finalRequestId);

                    return chain.filter(mutated);
                })
                .orElseGet(() -> {
                    log.warn("Невалидный JWT токен для пути: {} {}", request.getMethod(), path);
                    return unauthorized(exchange.getResponse(), "Невалидный или истёкший токен");
                });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private boolean isPublicPath(String path) {
        return publicPaths.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    private ServerWebExchange addRequestId(ServerWebExchange exchange, String requestId) {
        return exchange.mutate()
                .request(r -> r.header(HEADER_REQUEST_ID, requestId))
                .build();
    }

    private Mono<Void> unauthorized(ServerHttpResponse response, String reason) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("X-Error", reason);
        return response.setComplete();
    }
}

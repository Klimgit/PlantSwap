package com.plantswap.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plantswap.gateway.config.PublicPathsProperties;
import com.plantswap.gateway.security.JwtValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private final ObjectMapper objectMapper;
    private final List<String> publicPaths;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public JwtAuthFilter(JwtValidator jwtValidator,
                         ObjectMapper objectMapper,
                         PublicPathsProperties publicPathsProperties) {
        this.jwtValidator = jwtValidator;
        this.objectMapper = objectMapper;
        this.publicPaths = publicPathsProperties.publicPaths();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        HttpMethod method = Objects.requireNonNullElse(request.getMethod(), HttpMethod.GET);
        String query = exchange.getRequest().getURI().getRawQuery();

        String requestId = request.getHeaders().getFirst(HEADER_REQUEST_ID);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        final String finalRequestId = requestId;

        if (allowsAnonymousListingBrowse(method, path, query)) {
            log.debug("Публичный просмотр каталога объявлений: {} {}", method, path);
            return chain.filter(addRequestId(exchange, finalRequestId));
        }

        if (isPublicPath(path)) {
            log.debug("Публичный маршрут, JWT не требуется: {} {}", request.getMethod(), path);
            return chain.filter(addRequestId(exchange, finalRequestId));
        }

        String authHeader = request.getHeaders().getFirst(HEADER_AUTHORIZATION);
        String token = null;
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            token = authHeader.substring(BEARER_PREFIX.length());
        } else if (path.startsWith("/ws")) {
            token = extractAccessTokenFromQuery(query);
        }
        if (token == null || token.isBlank()) {
            log.warn("Отсутствует JWT (Authorization или access_token): {} {}", request.getMethod(), path);
            return unauthorized(exchange.getResponse(), "Требуется авторизация");
        }

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

    /**
     * GET /listings и GET /listings/{uuid} — без JWT, чтобы гости могли открывать каталог.
     * Не распространяется на /listings/favorites и вложенные пути (фото, избранное и т.д.).
     */
    private boolean allowsAnonymousListingBrowse(HttpMethod method, String path, String rawQuery) {
        if (!HttpMethod.GET.equals(method)) {
            return false;
        }
        if ("/listings".equals(path)) {
            // Запрос «мои объявления» требует JWT — иначе подделка X-User-Id или утечка списков
            if (rawQuery != null && rawQuery.contains("ownerId")) {
                return false;
            }
            return true;
        }
        if (!path.startsWith("/listings/")) {
            return false;
        }
        String tail = path.substring("/listings/".length());
        if (tail.isEmpty() || tail.contains("/")) {
            return false;
        }
        return !"favorites".equals(tail);
    }

    private ServerWebExchange addRequestId(ServerWebExchange exchange, String requestId) {
        return exchange.mutate()
                .request(r -> r.header(HEADER_REQUEST_ID, requestId))
                .build();
    }

    /** Query-параметр для SockJS; заголовок Authorization на первых GET часто недоступен в браузере. */
    private static String extractAccessTokenFromQuery(String rawQuery) {
        if (rawQuery == null || rawQuery.isBlank()) {
            return null;
        }
        for (String part : rawQuery.split("&")) {
            int eq = part.indexOf('=');
            if (eq <= 0) {
                continue;
            }
            String key = URLDecoder.decode(part.substring(0, eq), StandardCharsets.UTF_8);
            if ("access_token".equals(key)) {
                return URLDecoder.decode(part.substring(eq + 1), StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    private Mono<Void> unauthorized(ServerHttpResponse response, String reason) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("X-Error", reason);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("error", "UNAUTHORIZED");
        body.put("message", reason);
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(body);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.warn("Не удалось сериализовать ответ 401", e);
            return response.setComplete();
        }
    }
}

package com.plantswap.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Фильтр, который при каждом HTTP-запросе наполняет MDC (Mapped Diagnostic Context)
 * общими полями, автоматически появляющимися во всех строках лога:
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MdcRequestFilter extends OncePerRequestFilter {

    private static final String MDC_REQUEST_ID = "requestId";
    private static final String MDC_USER_ID    = "userId";
    private static final String MDC_METHOD     = "method";
    private static final String MDC_PATH       = "path";

    private static final String HEADER_REQUEST_ID = "X-Request-Id";
    private static final String HEADER_USER_ID    = "X-User-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        try {
            String requestId = request.getHeader(HEADER_REQUEST_ID);
            if (requestId == null || requestId.isBlank()) {
                requestId = UUID.randomUUID().toString();
            }

            MDC.put(MDC_REQUEST_ID, requestId);
            MDC.put(MDC_METHOD, request.getMethod());
            MDC.put(MDC_PATH, request.getRequestURI());

            String userId = request.getHeader(HEADER_USER_ID);
            if (userId != null && !userId.isBlank()) {
                MDC.put(MDC_USER_ID, userId);
            }

            response.setHeader(HEADER_REQUEST_ID, requestId);

            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}

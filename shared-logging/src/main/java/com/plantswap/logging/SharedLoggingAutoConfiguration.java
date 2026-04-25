package com.plantswap.logging;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

/**
 * Автоконфигурация shared-logging.
 * Регистрирует {@link MdcRequestFilter} с наивысшим приоритетом —
 * MDC-поля доступны во всех последующих фильтрах и обработчиках.
 */
@AutoConfiguration
public class SharedLoggingAutoConfiguration {

    @Bean
    public FilterRegistrationBean<MdcRequestFilter> mdcRequestFilter() {
        FilterRegistrationBean<MdcRequestFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new MdcRequestFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.setName("mdcRequestFilter");
        return registration;
    }
}

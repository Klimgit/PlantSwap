package com.plantswap.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "gateway")
public record PublicPathsProperties(List<String> publicPaths) {

    public PublicPathsProperties {
        if (publicPaths == null) publicPaths = List.of();
    }
}

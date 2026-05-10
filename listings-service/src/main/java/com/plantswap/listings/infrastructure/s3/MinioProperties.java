package com.plantswap.listings.infrastructure.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "minio")
public record MinioProperties(
        String url,
        String publicUrl,
        String accessKey,
        String secretKey,
        String bucket
) {
    /** База без завершающего слэша для сборки публичных URL объектов. */
    public String effectivePublicBaseUrl() {
        String base = (publicUrl != null && !publicUrl.isBlank()) ? publicUrl : url;
        if (base == null) return "";
        return base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
    }
}

package com.plantswap.listings.infrastructure.s3;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    private static final Logger log = LoggerFactory.getLogger(MinioConfig.class);

    @Bean
    public MinioClient minioClient(MinioProperties props) {
        return MinioClient.builder()
                .endpoint(props.url())
                .credentials(props.accessKey(), props.secretKey())
                .build();
    }

    @Bean
    public boolean ensureBucketExists(MinioClient client, MinioProperties props) {
        try {
            boolean exists = client.bucketExists(
                    BucketExistsArgs.builder().bucket(props.bucket()).build());
            if (!exists) {
                client.makeBucket(MakeBucketArgs.builder().bucket(props.bucket()).build());
                log.info("MinIO бакет создан: {}", props.bucket());
            }
        } catch (Exception e) {
            log.warn("Не удалось проверить/создать MinIO бакет: {}", e.getMessage());
        }
        return true;
    }
}

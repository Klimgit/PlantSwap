package com.plantswap.listings.infrastructure.s3;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
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

    /**
     * Создаёт бакет и включает анонимное чтение объектов (GetObject), иначе браузер
     * получает 403 по прямым URL вида http://localhost:9000/bucket/key.
     */
    @Bean
    public boolean ensureBucketExists(MinioClient client, MinioProperties props) {
        String bucket = props.bucket();
        try {
            boolean exists = client.bucketExists(
                    BucketExistsArgs.builder().bucket(bucket).build());
            if (!exists) {
                client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                log.info("MinIO бакет создан: {}", bucket);
            }
            applyPublicReadPolicy(client, bucket);
        } catch (Exception e) {
            log.warn("Не удалось проверить/настроить MinIO бакет {}: {}", bucket, e.getMessage());
        }
        return true;
    }

    private void applyPublicReadPolicy(MinioClient client, String bucket) throws Exception {
        String policy = """
                {
                  "Version": "2012-10-17",
                  "Statement": [
                    {
                      "Effect": "Allow",
                      "Principal": {"AWS": ["*"]},
                      "Action": ["s3:GetObject"],
                      "Resource": ["arn:aws:s3:::%s/*"]
                    }
                  ]
                }
                """.formatted(bucket);
        client.setBucketPolicy(SetBucketPolicyArgs.builder()
                .bucket(bucket)
                .config(policy)
                .build());
        log.info("Для бакета {} включено публичное чтение объектов (GetObject)", bucket);
    }
}

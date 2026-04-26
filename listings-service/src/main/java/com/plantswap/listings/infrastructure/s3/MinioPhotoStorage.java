package com.plantswap.listings.infrastructure.s3;

import com.plantswap.listings.application.port.out.PhotoStoragePort;
import com.plantswap.listings.domain.model.ListingId;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * Адаптер хранилища фотографий через MinIO .
 */
@Component
public class MinioPhotoStorage implements PhotoStoragePort {

    private static final Logger log = LoggerFactory.getLogger(MinioPhotoStorage.class);

    private final MinioClient minioClient;
    private final MinioProperties props;

    public MinioPhotoStorage(MinioClient minioClient, MinioProperties props) {
        this.minioClient = minioClient;
        this.props = props;
    }

    @Override
    public String upload(ListingId listingId, String filename,
                         InputStream data, long size, String contentType) {
        String s3Key = "listings/%s/%s".formatted(listingId, filename);
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(props.bucket())
                    .object(s3Key)
                    .stream(data, size, -1)
                    .contentType(contentType)
                    .build());
            log.debug("Фото загружено в MinIO: key={}", s3Key);
            return s3Key;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки фото в MinIO: " + s3Key, e);
        }
    }

    @Override
    public void delete(String s3Key) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(props.bucket())
                    .object(s3Key)
                    .build());
            log.debug("Фото удалено из MinIO: key={}", s3Key);
        } catch (Exception e) {
            log.warn("Не удалось удалить фото из MinIO: key={}", s3Key, e);
        }
    }

    @Override
    public String getPublicUrl(String s3Key) {
        return "%s/%s/%s".formatted(props.url(), props.bucket(), s3Key);
    }
}

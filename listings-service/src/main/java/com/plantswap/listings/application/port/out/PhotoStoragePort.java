package com.plantswap.listings.application.port.out;

import com.plantswap.listings.domain.model.ListingId;

import java.io.InputStream;

/**
 * Порт хранения фотографий объявлений в S3-совместимом хранилище.
 */
public interface PhotoStoragePort {

    String upload(ListingId listingId, String filename,
                  InputStream data, long size, String contentType);

    void delete(String s3Key);

    String getPublicUrl(String s3Key);
}

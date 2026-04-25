package com.plantswap.listings.domain.model;

public class Photo {

    private final PhotoId id;
    private final ListingId listingId;
    private final String s3Key;

    private int sortOrder;

    public Photo(PhotoId id, ListingId listingId, String s3Key, int sortOrder) {
        if (s3Key == null || s3Key.isBlank())
            throw new IllegalArgumentException("S3-ключ фото не может быть пустым");
        this.id = id;
        this.listingId = listingId;
        this.s3Key = s3Key;
        this.sortOrder = sortOrder;
    }

    public static Photo create(ListingId listingId, String s3Key, int sortOrder) {
        return new Photo(PhotoId.generate(), listingId, s3Key, sortOrder);
    }

    public void updateSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public PhotoId id() { return id; }
    public ListingId listingId() { return listingId; }
    public String s3Key() { return s3Key; }
    public int sortOrder() { return sortOrder; }
}

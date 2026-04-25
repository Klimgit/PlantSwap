package com.plantswap.listings.domain.model;

import com.plantswap.listings.domain.event.ListingClosedEvent;
import com.plantswap.listings.domain.event.ListingCreatedEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Агрегат Listing — корневая сущность контекста Catalog.
 */
public class Listing extends AggregateRoot {

    private final ListingId id;
    private final OwnerId ownerId;
    private ListingType type;
    private Title title;
    private Description description;
    private Price price;
    private City city;
    private ListingStatus status;
    private final List<Photo> photos;
    private final Instant createdAt;
    private Instant updatedAt;

    private Listing(ListingId id, OwnerId ownerId, ListingType type, Title title,
                    Description description, Price price, City city,
                    ListingStatus status, List<Photo> photos, Instant createdAt) {
        this.id = id;
        this.ownerId = ownerId;
        this.type = type;
        this.title = title;
        this.description = description;
        this.price = price;
        this.city = city;
        this.status = status;
        this.photos = new ArrayList<>(photos);
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    /**
     * Фабричный метод создания нового объявления.
     */
    public static Listing create(OwnerId ownerId, ListingType type, Title title,
                                  Description description, Price price, City city) {
        validatePrice(type, price);

        ListingId id = ListingId.generate();
        Instant now = Instant.now();
        Listing listing = new Listing(id, ownerId, type, title, description,
                price, city, ListingStatus.ACTIVE, List.of(), now);

        listing.registerEvent(ListingCreatedEvent.of(id, ownerId, type));
        return listing;
    }

    /**
     * Восстановление агрегата из базы данных. Не регистрирует событий.
     */
    public static Listing reconstitute(ListingId id, OwnerId ownerId, ListingType type,
                                        Title title, Description description, Price price,
                                        City city, ListingStatus status, List<Photo> photos,
                                        Instant createdAt, Instant updatedAt) {
        Listing listing = new Listing(id, ownerId, type, title, description,
                price, city, status, photos, createdAt);
        listing.updatedAt = updatedAt;
        return listing;
    }

    public void update(ListingType type, Title title, Description description,
                       Price price, City city) {
        ensureActive("редактировать");
        validatePrice(type, price);

        this.type = type;
        this.title = title;
        this.description = description;
        this.price = price;
        this.city = city;
        this.updatedAt = Instant.now();
    }

    public void close() {
        if (this.status == ListingStatus.CLOSED)
            throw new ListingAlreadyClosedException(id);
        this.status = ListingStatus.CLOSED;
        this.updatedAt = Instant.now();
        registerEvent(ListingClosedEvent.of(id, ownerId));
    }

    public Photo addPhoto(String s3Key) {
        ensureActive("добавлять фото к");
        if (photos.size() >= 10)
            throw new IllegalStateException("Объявление не может содержать более 10 фотографий");
        Photo photo = Photo.create(id, s3Key, photos.size());
        photos.add(photo);
        this.updatedAt = Instant.now();
        return photo;
    }

    public void removePhoto(PhotoId photoId) {
        ensureActive("удалять фото из");
        boolean removed = photos.removeIf(p -> p.id().equals(photoId));
        if (!removed)
            throw new IllegalArgumentException("Фото с id %s не найдено".formatted(photoId));
        for (int i = 0; i < photos.size(); i++) {
            photos.get(i).updateSortOrder(i);
        }
        this.updatedAt = Instant.now();
    }

    // ── Геттеры ────────────────────────────────────────────────────────────

    public ListingId id() { return id; }
    public OwnerId ownerId() { return ownerId; }
    public ListingType type() { return type; }
    public Title title() { return title; }
    public Description description() { return description; }
    public Price price() { return price; }
    public City city() { return city; }
    public ListingStatus status() { return status; }
    public List<Photo> photos() { return Collections.unmodifiableList(photos); }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public boolean isActive() { return status == ListingStatus.ACTIVE; }

    // ── Приватные методы ───────────────────────────────────────────────────

    private void ensureActive(String action) {
        if (status == ListingStatus.CLOSED)
            throw new ListingAlreadyClosedException(
                    "Нельзя %s закрытое объявление: %s".formatted(action, id));
    }

    private static void validatePrice(ListingType type, Price price) {
        if (type == ListingType.SELL && price == null)
            throw new IllegalArgumentException(
                    "Объявление типа SELL обязано содержать цену");
        if (type != ListingType.SELL && price != null)
            throw new IllegalArgumentException(
                    "Объявление типа %s не должно содержать цену".formatted(type));
    }
}

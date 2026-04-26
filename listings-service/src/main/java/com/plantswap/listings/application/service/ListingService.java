package com.plantswap.listings.application.service;

import com.plantswap.listings.application.command.CreateListingCommand;
import com.plantswap.listings.application.command.UpdateListingCommand;
import com.plantswap.listings.application.command.UploadPhotoCommand;
import com.plantswap.listings.application.port.in.*;
import com.plantswap.listings.application.port.out.EventPublisherPort;
import com.plantswap.listings.application.port.out.PhotoStoragePort;
import com.plantswap.listings.application.result.ListingDto;
import com.plantswap.listings.application.result.ListingSummaryDto;
import com.plantswap.listings.application.result.PageDto;
import com.plantswap.listings.application.result.PhotoDto;
import com.plantswap.listings.domain.model.*;
import com.plantswap.listings.domain.repository.FavoriteRepository;
import com.plantswap.listings.domain.repository.ListingFilter;
import com.plantswap.listings.domain.repository.ListingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ListingService implements
        CreateListingUseCase,
        UpdateListingUseCase,
        DeleteListingUseCase,
        GetListingUseCase,
        SearchListingsUseCase,
        GetUserListingsUseCase,
        UploadPhotoUseCase,
        RemovePhotoUseCase {

    private static final Logger log = LoggerFactory.getLogger(ListingService.class);

    private final ListingRepository listingRepository;
    private final FavoriteRepository favoriteRepository;
    private final PhotoStoragePort photoStorage;
    private final EventPublisherPort eventPublisher;

    public ListingService(ListingRepository listingRepository,
                          FavoriteRepository favoriteRepository,
                          PhotoStoragePort photoStorage,
                          EventPublisherPort eventPublisher) {
        this.listingRepository = listingRepository;
        this.favoriteRepository = favoriteRepository;
        this.photoStorage = photoStorage;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public ListingDto create(CreateListingCommand cmd) {
        ListingType type = ListingType.valueOf(cmd.type());
        Price price = buildPrice(cmd.priceAmount(), cmd.priceCurrency());

        Listing listing = Listing.create(
                OwnerId.of(cmd.ownerId()),
                type,
                new Title(cmd.title()),
                Description.ofNullable(cmd.description()),
                price,
                City.ofNullable(cmd.city())
        );
        listingRepository.save(listing);
        listing.pullDomainEvents().forEach(eventPublisher::publish);

        log.info("Объявление создано: id={}, ownerId={}, type={}", listing.id(), cmd.ownerId(), type);
        return toDto(listing, false);
    }

    @Override
    public ListingDto update(UpdateListingCommand cmd) {
        Listing listing = getOrThrow(ListingId.of(cmd.listingId()));
        checkOwnership(listing, cmd.requesterId());

        ListingType type = ListingType.valueOf(cmd.type());
        Price price = buildPrice(cmd.priceAmount(), cmd.priceCurrency());

        listing.update(type, new Title(cmd.title()),
                Description.ofNullable(cmd.description()), price,
                City.ofNullable(cmd.city()));
        listingRepository.save(listing);

        log.info("Объявление обновлено: id={}", listing.id());
        return toDto(listing, false);
    }

    @Override
    public void delete(UUID listingId, UUID requesterId) {
        Listing listing = getOrThrow(ListingId.of(listingId));
        checkOwnership(listing, requesterId);
        listing.close();
        listingRepository.save(listing);
        listing.pullDomainEvents().forEach(eventPublisher::publish);
        log.info("Объявление закрыто владельцем: id={}", listingId);
    }

    @Override
    @Transactional(readOnly = true)
    public ListingDto getListing(UUID listingId, UUID requesterId) {
        Listing listing = getOrThrow(ListingId.of(listingId));
        boolean isFav = requesterId != null &&
                favoriteRepository.exists(requesterId, listing.id());
        return toDto(listing, isFav);
    }

    @Override
    @Transactional(readOnly = true)
    public PageDto<ListingSummaryDto> search(ListingFilter filter, int page, int size) {
        List<Listing> listings = listingRepository.findAll(filter, page, size);
        long total = listingRepository.countAll(filter);
        return PageDto.of(listings.stream().map(this::toSummary).toList(), page, size, total);
    }

    @Override
    @Transactional(readOnly = true)
    public PageDto<ListingSummaryDto> getUserListings(UUID ownerId, int page, int size) {
        List<Listing> listings = listingRepository.findByOwnerId(OwnerId.of(ownerId), page, size);
        long total = listingRepository.countAll(
                new ListingFilter(null, null, null, null, null, null));
        return PageDto.of(listings.stream().map(this::toSummary).toList(), page, size, total);
    }

    @Override
    public PhotoDto uploadPhoto(UploadPhotoCommand cmd) {
        Listing listing = getOrThrow(ListingId.of(cmd.listingId()));
        checkOwnership(listing, cmd.requesterId());

        String s3Key = photoStorage.upload(
                listing.id(), cmd.filename(), cmd.data(), cmd.size(), cmd.contentType());

        Photo photo = listing.addPhoto(s3Key);
        listingRepository.save(listing);

        log.info("Фото загружено: listingId={}, photoId={}", cmd.listingId(), photo.id());
        return new PhotoDto(photo.id().value(), photoStorage.getPublicUrl(s3Key), photo.sortOrder());
    }

    @Override
    public void removePhoto(UUID listingId, UUID photoId, UUID requesterId) {
        Listing listing = getOrThrow(ListingId.of(listingId));
        checkOwnership(listing, requesterId);

        String s3Key = listing.photos().stream()
                .filter(p -> p.id().equals(PhotoId.of(photoId)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Фото не найдено: " + photoId))
                .s3Key();

        listing.removePhoto(PhotoId.of(photoId));
        listingRepository.save(listing);
        photoStorage.delete(s3Key);

        log.info("Фото удалено: listingId={}, photoId={}", listingId, photoId);
    }

    public void closeByDeal(UUID listingId) {
        listingRepository.findById(ListingId.of(listingId)).ifPresent(listing -> {
            if (listing.isActive()) {
                listing.close();
                listingRepository.save(listing);
                listing.pullDomainEvents().forEach(eventPublisher::publish);
                log.info("Объявление закрыто по событию DealCompleted: id={}", listingId);
            }
        });
    }

    // ── Маппинг ────────────────────────────────────────────────────────────

    private ListingDto toDto(Listing l, boolean isFavorite) {
        List<PhotoDto> photos = l.photos().stream()
                .map(p -> new PhotoDto(p.id().value(),
                        photoStorage.getPublicUrl(p.s3Key()), p.sortOrder()))
                .toList();
        return new ListingDto(
                l.id().value(), l.ownerId().value(),
                l.type().name(), l.title().value(),
                l.description().value(),
                l.price() != null ? l.price().amount() : null,
                l.price() != null ? l.price().currency() : null,
                l.city() != null ? l.city().value() : null,
                l.status().name(), photos, isFavorite,
                l.createdAt(), l.updatedAt()
        );
    }

    private ListingSummaryDto toSummary(Listing l) {
        return toSummaryPublic(l);
    }

    ListingSummaryDto toSummaryPublic(Listing l) {
        String firstPhotoUrl = l.photos().isEmpty() ? null :
                photoStorage.getPublicUrl(l.photos().getFirst().s3Key());
        return new ListingSummaryDto(
                l.id().value(), l.ownerId().value(),
                l.type().name(), l.title().value(),
                l.price() != null ? l.price().amount() : null,
                l.price() != null ? l.price().currency() : null,
                l.city() != null ? l.city().value() : null,
                l.status().name(), firstPhotoUrl, l.createdAt()
        );
    }

    // ── Вспомогательные методы ─────────────────────────────────────────────

    private Listing getOrThrow(ListingId id) {
        return listingRepository.findById(id)
                .orElseThrow(() -> ListingNotFoundException.byId(id));
    }

    private void checkOwnership(Listing listing, UUID requesterId) {
        if (!listing.ownerId().value().equals(requesterId))
            throw new ListingAccessDeniedException(listing.id(), OwnerId.of(requesterId));
    }

    private Price buildPrice(BigDecimal amount, String currency) {
        if (amount == null) return null;
        return new Price(amount, currency != null ? currency : "RUB");
    }
}

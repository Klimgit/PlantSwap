package com.plantswap.listings.api.rest;

import com.plantswap.listings.api.rest.dto.CreateListingRequest;
import com.plantswap.listings.api.rest.dto.UpdateListingRequest;
import com.plantswap.listings.application.command.CreateListingCommand;
import com.plantswap.listings.application.command.UpdateListingCommand;
import com.plantswap.listings.application.command.UploadPhotoCommand;
import com.plantswap.listings.application.result.ListingDto;
import com.plantswap.listings.application.result.ListingSummaryDto;
import com.plantswap.listings.application.result.PageDto;
import com.plantswap.listings.application.result.PhotoDto;
import com.plantswap.listings.application.service.FavoriteService;
import com.plantswap.listings.application.service.ListingService;
import com.plantswap.listings.domain.model.ListingType;
import com.plantswap.listings.domain.model.ListingStatus;
import com.plantswap.listings.domain.repository.ListingFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * REST API объявлений и избранного.
 * Идентификатор текущего пользователя передаётся шлюзом в заголовке {@code X-User-Id}.
 */
@RestController
@RequestMapping("/listings")
@Tag(name = "Объявления", description = "Каталог, фото, избранное")
public class ListingController {

    private static final int MAX_PAGE_SIZE = 100;

    private final ListingService listingService;
    private final FavoriteService favoriteService;

    public ListingController(ListingService listingService, FavoriteService favoriteService) {
        this.listingService = listingService;
        this.favoriteService = favoriteService;
    }

    @GetMapping(params = "!ownerId")
    @Operation(summary = "Поиск объявлений с фильтрами")
    public PageDto<ListingSummaryDto> search(
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        ListingFilter filter = buildSearchFilter(searchQuery, type, city, priceMin, priceMax);
        int safeSize = clampSize(size);
        return listingService.search(filter, page, safeSize);
    }

    @GetMapping(params = "ownerId")
    @Operation(summary = "Объявления пользователя по ownerId")
    public PageDto<ListingSummaryDto> listByOwner(
            @RequestParam UUID ownerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader("X-User-Id") UUID currentUserId) {

        if (!Objects.equals(ownerId, currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Можно запрашивать только свои объявления");
        }
        int safeSize = clampSize(size);
        return listingService.getUserListings(ownerId, page, safeSize);
    }

    @GetMapping("/favorites")
    @Operation(summary = "Избранные объявления текущего пользователя")
    public PageDto<ListingSummaryDto> favorites(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return favoriteService.getFavorites(userId, page, clampSize(size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Карточка объявления")
    public ListingDto getOne(
            @PathVariable("id") UUID id,
            @RequestHeader(value = "X-User-Id", required = false) UUID viewerId) {
        return listingService.getListing(id, viewerId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать объявление")
    public ListingDto create(
            @Valid @RequestBody CreateListingRequest body,
            @RequestHeader("X-User-Id") UUID ownerId) {
        return listingService.create(new CreateListingCommand(
                ownerId,
                body.type(),
                body.title(),
                body.description(),
                body.priceAmount(),
                body.priceCurrency(),
                body.city()
        ));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить объявление")
    public ListingDto update(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateListingRequest body,
            @RequestHeader("X-User-Id") UUID userId) {
        return listingService.update(new UpdateListingCommand(
                id,
                userId,
                body.type(),
                body.title(),
                body.description(),
                body.priceAmount(),
                body.priceCurrency(),
                body.city()
        ));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Закрыть объявление (владелец)")
    public void delete(@PathVariable("id") UUID id, @RequestHeader("X-User-Id") UUID userId) {
        listingService.delete(id, userId);
    }

    @PostMapping(value = "/{id}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Загрузить фото")
    public PhotoDto uploadPhoto(
            @PathVariable("id") UUID listingId,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestPart("file") MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Файл не передан");
        }
        String name = file.getOriginalFilename();
        if (name == null || name.isBlank()) {
            name = "photo";
        }
        return listingService.uploadPhoto(new UploadPhotoCommand(
                listingId,
                userId,
                name,
                file.getContentType() != null ? file.getContentType() : "application/octet-stream",
                file.getSize(),
                file.getInputStream()
        ));
    }

    @DeleteMapping("/{listingId}/photos/{photoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить фото")
    public void removePhoto(
            @PathVariable UUID listingId,
            @PathVariable UUID photoId,
            @RequestHeader("X-User-Id") UUID userId) {
        listingService.removePhoto(listingId, photoId, userId);
    }

    @PostMapping("/{id}/favorites")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Добавить в избранное")
    public void addFavorite(@PathVariable("id") UUID listingId, @RequestHeader("X-User-Id") UUID userId) {
        favoriteService.addToFavorites(userId, listingId);
    }

    @DeleteMapping("/{id}/favorites")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Убрать из избранного")
    public void removeFavorite(@PathVariable("id") UUID listingId, @RequestHeader("X-User-Id") UUID userId) {
        favoriteService.removeFromFavorites(userId, listingId);
    }

    // ── Вспомогательные методы ───────────────────────────────────────────────

    private static int clampSize(int size) {
        if (size < 1) return 20;
        return Math.min(size, MAX_PAGE_SIZE);
    }

    private static ListingFilter buildSearchFilter(String searchQuery, String type, String city,
                                                   BigDecimal priceMin, BigDecimal priceMax) {
        ListingType listingType = null;
        if (type != null && !type.isBlank()) {
            try {
                listingType = ListingType.valueOf(type.trim());
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Неизвестный тип объявления: " + type);
            }
        }
        String cityTrim = city != null && !city.isBlank() ? city.trim() : null;
        String sq = searchQuery != null && !searchQuery.isBlank() ? searchQuery.trim() : null;

        return new ListingFilter(sq, listingType, ListingStatus.ACTIVE, city, priceMin, priceMax);
    }
}

package com.plantswap.listings.api.rest;

import com.plantswap.listings.api.rest.dto.CreateListingRequest;
import com.plantswap.listings.api.rest.dto.PageResponse;
import com.plantswap.listings.api.rest.dto.UpdateListingRequest;
import com.plantswap.listings.application.command.CreateListingCommand;
import com.plantswap.listings.application.command.UpdateListingCommand;
import com.plantswap.listings.application.command.UploadPhotoCommand;
import com.plantswap.listings.application.port.in.*;
import com.plantswap.listings.application.result.ListingDto;
import com.plantswap.listings.application.result.ListingSummaryDto;
import com.plantswap.listings.application.result.PageDto;
import com.plantswap.listings.application.result.PhotoDto;
import com.plantswap.listings.domain.model.ListingStatus;
import com.plantswap.listings.domain.model.ListingType;
import com.plantswap.listings.domain.repository.ListingFilter;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/listings")
public class ListingController {

    private final CreateListingUseCase createListingUseCase;
    private final UpdateListingUseCase updateListingUseCase;
    private final DeleteListingUseCase deleteListingUseCase;
    private final GetListingUseCase getListingUseCase;
    private final SearchListingsUseCase searchListingsUseCase;
    private final GetUserListingsUseCase getUserListingsUseCase;
    private final UploadPhotoUseCase uploadPhotoUseCase;
    private final RemovePhotoUseCase removePhotoUseCase;
    private final FavoriteUseCase favoriteUseCase;

    public ListingController(CreateListingUseCase createListingUseCase,
                               UpdateListingUseCase updateListingUseCase,
                               DeleteListingUseCase deleteListingUseCase,
                               GetListingUseCase getListingUseCase,
                               SearchListingsUseCase searchListingsUseCase,
                               GetUserListingsUseCase getUserListingsUseCase,
                               UploadPhotoUseCase uploadPhotoUseCase,
                               RemovePhotoUseCase removePhotoUseCase,
                               FavoriteUseCase favoriteUseCase) {
        this.createListingUseCase = createListingUseCase;
        this.updateListingUseCase = updateListingUseCase;
        this.deleteListingUseCase = deleteListingUseCase;
        this.getListingUseCase = getListingUseCase;
        this.searchListingsUseCase = searchListingsUseCase;
        this.getUserListingsUseCase = getUserListingsUseCase;
        this.uploadPhotoUseCase = uploadPhotoUseCase;
        this.removePhotoUseCase = removePhotoUseCase;
        this.favoriteUseCase = favoriteUseCase;
    }

    @GetMapping("/favorites")
    public PageResponse<ListingSummaryDto> favorites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader("X-User-Id") UUID userId) {
        PageDto<ListingSummaryDto> p = favoriteUseCase.getFavorites(userId, page, size);
        return new PageResponse<>(p.content(), p.page(), p.size(), p.totalElements(), p.totalPages());
    }

    @GetMapping
    public PageResponse<ListingSummaryDto> listOrSearch(
            @RequestParam(required = false) UUID ownerId,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (ownerId != null) {
            PageDto<ListingSummaryDto> p = getUserListingsUseCase.getUserListings(ownerId, page, size);
            return new PageResponse<>(p.content(), p.page(), p.size(), p.totalElements(), p.totalPages());
        }
        ListingFilter filter = new ListingFilter(
                searchQuery,
                type != null ? ListingType.valueOf(type) : null,
                ListingStatus.ACTIVE,
                city,
                priceMin,
                priceMax
        );
        PageDto<ListingSummaryDto> p = searchListingsUseCase.search(filter, page, size);
        return new PageResponse<>(p.content(), p.page(), p.size(), p.totalElements(), p.totalPages());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ListingDto create(@Valid @RequestBody CreateListingRequest req,
                             @RequestHeader("X-User-Id") UUID ownerId) {
        return createListingUseCase.create(new CreateListingCommand(
                ownerId,
                req.type(),
                req.title(),
                req.description(),
                req.priceAmount(),
                req.priceCurrency(),
                req.city()
        ));
    }

    @GetMapping("/{id}")
    public ListingDto get(@PathVariable UUID id,
                          @RequestHeader(value = "X-User-Id", required = false) UUID requesterId) {
        return getListingUseCase.getListing(id, requesterId);
    }

    @PutMapping("/{id}")
    public ListingDto update(@PathVariable UUID id,
                             @Valid @RequestBody UpdateListingRequest req,
                             @RequestHeader("X-User-Id") UUID requesterId) {
        return updateListingUseCase.update(new UpdateListingCommand(
                id,
                requesterId,
                req.type(),
                req.title(),
                req.description(),
                req.priceAmount(),
                req.priceCurrency(),
                req.city()
        ));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id,
                       @RequestHeader("X-User-Id") UUID requesterId) {
        deleteListingUseCase.delete(id, requesterId);
    }

    @PostMapping(value = "/{id}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PhotoDto uploadPhoto(@PathVariable UUID id,
                                @RequestHeader("X-User-Id") UUID requesterId,
                                @RequestParam("file") MultipartFile file) throws IOException {
        return uploadPhotoUseCase.uploadPhoto(new UploadPhotoCommand(
                id,
                requesterId,
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                file.getInputStream()
        ));
    }

    @DeleteMapping("/{listingId}/photos/{photoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removePhoto(@PathVariable UUID listingId,
                            @PathVariable UUID photoId,
                            @RequestHeader("X-User-Id") UUID requesterId) {
        removePhotoUseCase.removePhoto(listingId, photoId, requesterId);
    }

    @PostMapping("/{id}/favorites")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addFavorite(@PathVariable UUID id,
                            @RequestHeader("X-User-Id") UUID userId) {
        favoriteUseCase.addToFavorites(userId, id);
    }

    @DeleteMapping("/{id}/favorites")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFavorite(@PathVariable UUID id,
                               @RequestHeader("X-User-Id") UUID userId) {
        favoriteUseCase.removeFromFavorites(userId, id);
    }
}

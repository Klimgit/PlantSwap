package com.plantswap.listings.application.command;

import java.io.InputStream;
import java.util.UUID;

public record UploadPhotoCommand(
        UUID listingId,
        UUID requesterId,
        String filename,
        String contentType,
        long size,
        InputStream data
) {}

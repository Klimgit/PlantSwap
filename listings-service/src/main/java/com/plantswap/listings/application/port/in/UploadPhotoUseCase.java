package com.plantswap.listings.application.port.in;

import com.plantswap.listings.application.command.UploadPhotoCommand;
import com.plantswap.listings.application.result.PhotoDto;

public interface UploadPhotoUseCase {
    PhotoDto uploadPhoto(UploadPhotoCommand command);
}

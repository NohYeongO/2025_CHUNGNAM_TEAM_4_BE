package com.chungnam.eco.common.storage;

import com.chungnam.eco.challenge.domain.Challenge;
import com.chungnam.eco.challenge.domain.ChallengeImage;

public record ImageUploadDto(
        String originalFileName,
        String storedFileName,
        String imageUrl,
        Integer sort
) {
    public static ChallengeImage toEntity(Challenge challenge, ImageUploadDto imageUploadDto) {
        return ChallengeImage.builder()
                .challenge(challenge)
                .originalName(imageUploadDto.originalFileName())
                .storedName(imageUploadDto.storedFileName())
                .url(imageUploadDto.imageUrl())
                .sort(imageUploadDto.sort())
                .build();
    }
}

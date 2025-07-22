package com.chungnam.eco.common.storage;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.chungnam.eco.common.exception.ImageUploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class AzureBlobStorageService {
    private final BlobServiceClient blobServiceClient;
    private final String containerName;

    public List<ImageUploadDto> uploadImages(List<MultipartFile> multipartFiles) {
        validateImages(multipartFiles);
        List<ImageUploadDto> responseDtos = new ArrayList<>();
        int sort = 1;

        for (MultipartFile file : multipartFiles) {
            try {
                String originalFileName = file.getOriginalFilename();
                long millis = System.currentTimeMillis();
                int randomNumber = ThreadLocalRandom.current().nextInt(1, 100);
                String storedFileName = String.format("%d-%02d-%s", millis, randomNumber, originalFileName);

                BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
                BlobClient blobClient = containerClient.getBlobClient(storedFileName);

                blobClient.upload(file.getInputStream(), file.getSize(), true);

                BlobHttpHeaders headers = new BlobHttpHeaders().setContentType(file.getContentType());
                blobClient.setHttpHeaders(headers);

                responseDtos.add(new ImageUploadDto(
                        originalFileName,
                        storedFileName,
                        blobClient.getBlobUrl(),
                        sort++
                ));

            } catch (IOException e) {
                log.error("Failed to upload file: {}", file.getOriginalFilename(), e);
                throw new ImageUploadException();
            }
        }

        return responseDtos;
    }

    private void validateImages(List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            throw new ImageUploadException("이미지는 최소 1장 이상 업로드해야 합니다.");
        }

        if (images.size() > 3) {
            throw new ImageUploadException("이미지는 최대 3장까지 업로드 가능합니다.");
        }

        for (MultipartFile image : images) {
            if (image.isEmpty()) {
                throw new ImageUploadException("빈 이미지 파일은 업로드할 수 없습니다.");
            }
            // 파일 크기 검증 (10MB 제한)
            if (image.getSize() > 10 * 1024 * 1024) {
                throw new ImageUploadException("이미지 파일 크기는 10MB 이하여야 합니다.");
            }
            // 파일 형식 검증
            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new ImageUploadException("이미지 파일만 업로드 가능합니다.");
            }
        }
    }
}

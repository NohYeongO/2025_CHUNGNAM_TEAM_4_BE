package com.chungnam.eco.community.service;

import com.chungnam.eco.common.exception.ImageUploadException;
import com.chungnam.eco.common.storage.AzureBlobStorageService;
import com.chungnam.eco.common.storage.ImageUploadDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityImageService {

    private final AzureBlobStorageService azureBlobStorageService;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    /**
     * 커뮤니티 이미지 업로드 (다중 파일)
     */
    public List<String> uploadImages(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return new ArrayList<>();
        }

        // 빈 파일 필터링
        List<MultipartFile> validFiles = files.stream()
                .filter(file -> file != null && !file.isEmpty())
                .toList();

        if (validFiles.isEmpty()) {
            return new ArrayList<>();
        }

        // 파일 개수 제한 (최대 5개)
        if (validFiles.size() > 5) {
            throw new ImageUploadException("이미지는 최대 5장까지 업로드 가능합니다.");
        }

        // 각 파일 유효성 검사
        for (MultipartFile file : validFiles) {
            validateImage(file);
        }

        try {
            List<ImageUploadDto> uploadResults = azureBlobStorageService.uploadImages(validFiles);
            List<String> imageUrls = uploadResults.stream()
                    .map(ImageUploadDto::imageUrl)
                    .toList();
            
            log.info("커뮤니티 이미지 업로드 성공: {} 개 파일", imageUrls.size());
            return imageUrls;
        } catch (Exception e) {
            log.error("커뮤니티 이미지 업로드 실패", e);
            throw new ImageUploadException("이미지 업로드에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 단일 이미지 업로드
     */
    public String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ImageUploadException("업로드할 이미지가 없습니다.");
        }

        validateImage(file);

        try {
            List<ImageUploadDto> uploadResults = azureBlobStorageService.uploadImages(Collections.singletonList(file));
            if (uploadResults.isEmpty()) {
                throw new ImageUploadException("이미지 업로드 결과가 없습니다.");
            }
            
            String imageUrl = uploadResults.get(0).imageUrl();
            log.info("커뮤니티 단일 이미지 업로드 성공: {}", imageUrl);
            return imageUrl;
        } catch (Exception e) {
            log.error("커뮤니티 단일 이미지 업로드 실패: {}", file.getOriginalFilename(), e);
            throw new ImageUploadException("이미지 업로드에 실패했습니다: " + file.getOriginalFilename());
        }
    }

    /**
     * 이미지 파일 유효성 검사
     */
    private void validateImage(MultipartFile file) {
        // 파일 크기 검사
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ImageUploadException("파일 크기가 너무 큽니다. 최대 10MB까지 업로드 가능합니다.");
        }

        // 파일 확장자 검사
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new ImageUploadException("파일 이름이 올바르지 않습니다.");
        }

        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new ImageUploadException("지원하지 않는 파일 형식입니다. 지원 형식: " + String.join(", ", ALLOWED_EXTENSIONS));
        }

        // Content Type 검사
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ImageUploadException("이미지 파일만 업로드 가능합니다.");
        }
    }

    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }
}

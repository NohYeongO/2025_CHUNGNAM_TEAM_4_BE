package com.chungnam.eco.point.controller;

import com.chungnam.eco.common.security.AuthenticationHelper;
import com.chungnam.eco.point.service.GenerateQRService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/generate-qr")
@RequiredArgsConstructor
public class GenerateQRController {

    private final GenerateQRService generateQRService;

    @GetMapping(produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateUserQR() throws Exception {
        Long userId = AuthenticationHelper.getCurrentUserId();
        byte[] QR = generateQRService.createQR(userId);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(QR);
    }
}

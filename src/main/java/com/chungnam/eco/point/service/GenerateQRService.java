package com.chungnam.eco.point.service;

import com.chungnam.eco.common.exception.UserNotFoundException;
import com.chungnam.eco.common.jwt.JwtProvider;
import com.chungnam.eco.user.domain.User;
import com.chungnam.eco.user.repository.UserJPARepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenerateQRService {
    private final UserJPARepository userRepository;
    private final JwtProvider jwtProvider;

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));
    }

    // qr 생성
    public byte[] createQR(Long userId) throws IOException, WriterException {
        User user = findUser(userId);

        String QRToken = jwtProvider.generateQRToken(user.getId(), user.getRole().name());

        // QR 코드 생성
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(QRToken, BarcodeFormat.QR_CODE, 300, 300);

        // 이미지로 변환
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);

        return pngOutputStream.toByteArray();
    }
}

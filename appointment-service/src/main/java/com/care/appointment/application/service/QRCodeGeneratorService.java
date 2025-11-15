package com.care.appointment.application.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;

/**
 * Service for generating QR codes and verification codes for appointments
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class QRCodeGeneratorService {

    private static final int QR_CODE_WIDTH = 300;
    private static final int QR_CODE_HEIGHT = 300;
    private static final Random random = new Random();

    @Value("${app.appointment.verification-code-length:3}")
    private int verificationCodeLength;

    @Value("${app.appointment.qr-code-expiry-minutes:1440}")
    private int qrCodeExpiryMinutes; // Default 24 hours

    /**
     * Generate a QR code containing the appointment code and appointment ID
     *
     * @param appointmentCode The appointment code (e.g., HQ-2025-0001)
     * @param appointmentId The appointment UUID
     * @return Base64 encoded QR code image
     */
    public String generateQRCode(String appointmentCode, UUID appointmentId) {
        try {
            // Create QR content with both code and ID
            String qrContent = String.format("APPT:%s|ID:%s", appointmentCode, appointmentId.toString());

            // Generate QR code matrix
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(qrContent, BarcodeFormat.QR_CODE, QR_CODE_WIDTH, QR_CODE_HEIGHT);

            // Convert to BufferedImage
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            // Convert to PNG bytes
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            byte[] qrImageBytes = outputStream.toByteArray();

            // Encode to Base64
            String base64QR = Base64.getEncoder().encodeToString(qrImageBytes);

            log.info("Generated QR code for appointment: {}", appointmentCode);

            return "data:image/png;base64," + base64QR;

        } catch (Exception e) {
            log.error("Error generating QR code for appointment: {}", appointmentCode, e);
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }

    /**
     * Generate a simple 3-digit verification code
     * Format: X-Y-Z (e.g., 4-2-7)
     *
     * @return Verification code as string
     */
    public String generateVerificationCode() {
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < verificationCodeLength; i++) {
            int digit = random.nextInt(10);
            code.append(digit);
            if (i < verificationCodeLength - 1) {
                code.append("-");
            }
        }

        return code.toString();
    }

    /**
     * Verify that a provided code matches the stored verification code
     *
     * @param providedCode The code provided by user
     * @param storedCode The stored verification code
     * @return true if codes match (case-insensitive)
     */
    public boolean verifyCode(String providedCode, String storedCode) {
        if (providedCode == null || storedCode == null) {
            return false;
        }
        // Normalize by removing spaces and hyphens
        String normalizedProvided = providedCode.replaceAll("[\\s-]", "");
        String normalizedStored = storedCode.replaceAll("[\\s-]", "");

        return normalizedProvided.equalsIgnoreCase(normalizedStored);
    }

    /**
     * Generate appointment QR data combining code and verification
     *
     * @param appointmentCode Appointment code
     * @param appointmentId Appointment ID
     * @return QR code string
     */
    public String generateCompleteQR(String appointmentCode, UUID appointmentId) {
        return generateQRCode(appointmentCode, appointmentId);
    }

    /**
     * Get QR code expiry time in minutes
     */
    public int getQRCodeExpiryMinutes() {
        return qrCodeExpiryMinutes;
    }
}

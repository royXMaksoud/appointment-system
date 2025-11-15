package com.care.appointment.presentation.controller;

import com.care.appointment.application.dto.AppointmentQRDTO;
import com.care.appointment.application.dto.VerifyAppointmentRequest;
import com.care.appointment.application.dto.VerifyAppointmentResponse;
import com.care.appointment.application.service.AppointmentVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller for appointment verification via QR code, appointment code, or verification code
 */
@RestController
@RequestMapping("/api/v1/appointments/verify")
@Tag(name = "Appointment Verification", description = "Verify appointments using QR code, appointment code, or verification code")
@RequiredArgsConstructor
@Slf4j
public class AppointmentVerificationController {

    private final AppointmentVerificationService verificationService;

    /**
     * Verify appointment by code (e.g., HQ-2025-0001)
     */
    @PostMapping("/by-code")
    @Operation(summary = "Verify appointment by code", description = "Verify appointment using appointment code like HQ-2025-0001")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Appointment verified successfully"),
        @ApiResponse(responseCode = "404", description = "Appointment not found"),
        @ApiResponse(responseCode = "400", description = "Invalid code format")
    })
    public ResponseEntity<VerifyAppointmentResponse> verifyByCode(
        @Valid @RequestBody VerifyAppointmentRequest request
    ) {
        log.info("Verifying appointment by code: {}", request.getAppointmentCodeOrId());
        VerifyAppointmentResponse response = verificationService.verifyByAppointmentCode(request.getAppointmentCodeOrId());
        return ResponseEntity.ok(response);
    }

    /**
     * Verify appointment by appointment ID (UUID)
     */
    @GetMapping("/{appointmentId}")
    @Operation(summary = "Get appointment QR and verification code", description = "Retrieve QR code and verification code for an appointment")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Appointment found"),
        @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    public ResponseEntity<AppointmentQRDTO> getAppointmentQR(
        @Parameter(description = "Appointment ID", required = true)
        @PathVariable UUID appointmentId
    ) {
        log.info("Retrieving QR data for appointment: {}", appointmentId);
        AppointmentQRDTO qrData = verificationService.getAppointmentQRData(appointmentId);
        return ResponseEntity.ok(qrData);
    }

    /**
     * Verify appointment using verification code
     * For accessibility: beneficiary provides 3 digits from their card
     */
    @PostMapping("/by-verification-code")
    @Operation(summary = "Verify appointment by verification code", description = "Simple 3-digit code verification for accessibility (for illiterate users)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Verification code valid"),
        @ApiResponse(responseCode = "401", description = "Invalid verification code"),
        @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    public ResponseEntity<VerifyAppointmentResponse> verifyByVerificationCode(
        @Valid @RequestBody VerifyAppointmentRequest request
    ) {
        log.info("Verifying appointment by verification code");
        VerifyAppointmentResponse response = verificationService.verifyByVerificationCode(
            request.getAppointmentCodeOrId(),
            request.getVerificationCode()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Verify appointment by QR content
     */
    @PostMapping("/by-qr")
    @Operation(summary = "Verify appointment by QR content", description = "Verify appointment using decoded QR content")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "QR code verified"),
        @ApiResponse(responseCode = "400", description = "Invalid QR format"),
        @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    public ResponseEntity<VerifyAppointmentResponse> verifyByQR(
        @Valid @RequestBody VerifyAppointmentRequest request
    ) {
        log.info("Verifying appointment by QR code");
        VerifyAppointmentResponse response = verificationService.verifyByQRContent(request.getAppointmentCodeOrId());
        return ResponseEntity.ok(response);
    }

    /**
     * Get QR code as image/PNG (for display or printing)
     */
    @GetMapping("/{appointmentId}/qr-image")
    @Operation(summary = "Get appointment QR code as image", description = "Returns QR code image in PNG format")
    @ApiResponse(responseCode = "200", description = "QR code image", content = @Content(mediaType = "image/png"))
    public ResponseEntity<byte[]> getQRCodeImage(
        @Parameter(description = "Appointment ID", required = true)
        @PathVariable UUID appointmentId
    ) {
        log.info("Generating QR image for appointment: {}", appointmentId);
        byte[] qrImage = verificationService.getQRCodeImage(appointmentId);
        return ResponseEntity.ok()
            .header("Content-Type", "image/png")
            .body(qrImage);
    }

    /**
     * Resend QR code to beneficiary via SMS or email
     */
    @PostMapping("/{appointmentId}/resend-qr")
    @Operation(summary = "Resend QR code to beneficiary", description = "Resend QR code and verification code via SMS or email")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "QR code sent successfully"),
        @ApiResponse(responseCode = "404", description = "Appointment not found"),
        @ApiResponse(responseCode = "400", description = "Invalid notification method")
    })
    public ResponseEntity<String> resendQR(
        @Parameter(description = "Appointment ID", required = true)
        @PathVariable UUID appointmentId,
        @Parameter(description = "Notification method (SMS, EMAIL, PUSH)", required = true)
        @RequestParam(value = "method", defaultValue = "SMS") String method
    ) {
        log.info("Resending QR code for appointment: {} via {}", appointmentId, method);
        verificationService.resendQRCode(appointmentId, method);
        return ResponseEntity.ok("QR code sent successfully via " + method);
    }
}

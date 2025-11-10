package com.care.appointment.web.dto.admin.beneficiary;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBeneficiaryRequest {

    @NotBlank(message = "National ID is required")
    @Size(max = 50, message = "National ID must not exceed 50 characters")
    private String nationalId;

    @NotBlank(message = "Full name is required")
    @Size(max = 200, message = "Full name must not exceed 200 characters")
    private String fullName;

    @Size(max = 200, message = "Mother name must not exceed 200 characters")
    private String motherName;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid mobile number format (use E.164 format)")
    @Size(max = 20, message = "Mobile number must not exceed 20 characters")
    private String mobileNumber;

    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Size(max = 1000, message = "Address must not exceed 1000 characters")
    private String address;

    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private Double latitude;

    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private Double longitude;

    private Boolean isActive;

    private UUID createdById;
}


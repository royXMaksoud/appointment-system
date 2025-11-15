package com.care.appointment.web.controller.admin;

import com.care.appointment.domain.model.NearestServiceCenterOption;
import com.care.appointment.domain.ports.in.appointment.SuggestAppointmentUseCase;
import com.care.appointment.web.dto.admin.appointment.NearestServiceCenterRequest;
import com.care.appointment.web.dto.admin.appointment.NearestServiceCenterResponse;
import com.care.appointment.web.mapper.AppointmentSuggestionWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/appointments/nearest")
@RequiredArgsConstructor
@Tag(name = "Appointment Suggestions", description = "APIs for suggesting nearest centers based on location or availability")
@Slf4j
public class AppointmentSuggestionController {

    private final SuggestAppointmentUseCase suggestAppointmentUseCase;
    private final AppointmentSuggestionWebMapper mapper;

    @PostMapping("/location")
    @Operation(summary = "Find nearest centers by location",
            description = "Returns the nearest centers that provide the selected service type ordered by distance",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Suggestions returned successfully",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = NearestServiceCenterResponse.class))))
            })
    public ResponseEntity<List<NearestServiceCenterResponse>> findNearestByLocation(
            @Valid @RequestBody NearestServiceCenterRequest request) {
        List<NearestServiceCenterOption> options = suggestAppointmentUseCase.findNearestByLocation(mapper.toQuery(request));
        List<NearestServiceCenterResponse> response = options.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/availability")
    @Operation(summary = "Find nearest centers by earliest availability",
            description = "Returns centers ordered by the earliest available appointment regardless of distance",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Suggestions returned successfully",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = NearestServiceCenterResponse.class))))
            })
    public ResponseEntity<List<NearestServiceCenterResponse>> findNearestByAvailability(
            @Valid @RequestBody NearestServiceCenterRequest request) {
        List<NearestServiceCenterOption> options = suggestAppointmentUseCase.findNearestByAvailability(mapper.toQuery(request));
        List<NearestServiceCenterResponse> response = options.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}




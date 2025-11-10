// file: auth-service/src/main/java/com/ftp/authservice/web/controller/UserController.java
package com.ftp.authservice.web.controller;

import com.ftp.authservice.application.user.command.CreateUserCommand;
import com.ftp.authservice.application.user.command.UpdateUserCommand;
import com.ftp.authservice.domain.model.User;
import com.ftp.authservice.domain.ports.in.user.DeleteUseCase;
import com.ftp.authservice.domain.ports.in.user.LoadAllUseCase;
import com.ftp.authservice.domain.ports.in.user.LoadUseCase;
import com.ftp.authservice.domain.ports.in.user.SaveUseCase;
import com.ftp.authservice.domain.ports.in.user.UpdateUseCase;
import com.ftp.authservice.web.dto.user.CreateUserRequest;
import com.ftp.authservice.web.dto.user.UpdateUserRequest;
import com.ftp.authservice.web.dto.user.UserResponse;
import com.ftp.authservice.web.mapper.UserWebMapper;
import com.sharedlib.core.filter.FilterMeta;
import com.sharedlib.core.filter.FilterMetaFactory;
import com.sharedlib.core.filter.FilterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    private final SaveUseCase saveUserUseCase;
    private final UpdateUseCase updateUserUseCase;
    private final LoadUseCase loadUserUseCase;
    private final DeleteUseCase deleteUserUseCase;
    private final LoadAllUseCase loadAllUsersUseCase;
    private final UserWebMapper mapper;

    @PostMapping
    @Operation(summary = "Create a new user")
    @ApiResponse(responseCode = "201", description = "User created",
            content = @Content(schema = @Schema(implementation = UserResponse.class)))
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        CreateUserCommand command = mapper.toCreateCommand(request);
        User created = saveUserUseCase.saveUser(command);
        UserResponse body = mapper.toResponse(created);
        return ResponseEntity
                .created(URI.create("/api/users/" + body.getId()))
                .body(body);
    }

    @PutMapping("/{userId:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Update an existing user")
    @ApiResponse(responseCode = "200", description = "User updated",
            content = @Content(schema = @Schema(implementation = UserResponse.class)))
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        UpdateUserCommand command = mapper.toUpdateCommand(userId, request);
        User updated = updateUserUseCase.updateUser(command);
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @GetMapping("/{userId:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Get user by ID")
    @ApiResponse(responseCode = "200", description = "User found",
            content = @Content(schema = @Schema(implementation = UserResponse.class)))
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID userId) {
        Optional<User> user = loadUserUseCase.getUserById(userId);
        return user.map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all users (paged)")
    @ApiResponse(responseCode = "200", description = "Users retrieved")
    public ResponseEntity<Page<UserResponse>> getAllUsers(@PageableDefault(size = 10) Pageable pageable) {
        FilterRequest safe = new FilterRequest();
        Page<User> page = loadAllUsersUseCase.loadAllUsers(safe, pageable);
        return ResponseEntity.ok(page.map(mapper::toResponse));
    }

    @DeleteMapping("/{userId:[0-9a-fA-F\\-]{36}}")
    @Operation(summary = "Delete user by ID")
    @ApiResponse(responseCode = "204", description = "User deleted")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        deleteUserUseCase.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/filter")
    @Operation(summary = "Filter users (POST)")
    @ApiResponse(responseCode = "200", description = "Users filtered")
    public ResponseEntity<Page<UserResponse>> filterUsersPost(
            @RequestBody(required = false) FilterRequest request,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        FilterRequest safe = (request != null) ? request : new FilterRequest();
        Page<UserResponse> page = loadAllUsersUseCase
                .loadAllUsers(safe, pageable)
                .map(mapper::toResponse);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter users (GET)")
    public ResponseEntity<Page<UserResponse>> filterUsersGet(
            @RequestParam(name = "q", required = false) String q,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        FilterRequest safe = new FilterRequest();
        Page<UserResponse> page = loadAllUsersUseCase
                .loadAllUsers(safe, pageable)
                .map(mapper::toResponse);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/meta")
    @Operation(summary = "Get user filter metadata")
    public ResponseEntity<FilterMeta> getUserMeta() {
        var meta = FilterMetaFactory.build(
                com.ftp.authservice.infrastructure.db.entities.UserJpaEntity.class,
                com.ftp.authservice.infrastructure.config.UserFilterConfig.ALLOWED_FIELDS,
                com.ftp.authservice.infrastructure.config.UserFilterConfig.SORTABLE,
                com.ftp.authservice.infrastructure.config.UserFilterConfig.DEFAULT_PAGE_SIZE,
                Map.of(),
                null
        );
        String etag = Integer.toHexString(meta.hashCode());
        return ResponseEntity.ok()
                .eTag(etag)
                .cacheControl(org.springframework.http.CacheControl.maxAge(java.time.Duration.ofHours(1)).cachePublic())
                .body(meta);
    }
}

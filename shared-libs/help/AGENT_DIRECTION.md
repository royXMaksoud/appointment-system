# 🤖 Agent Direction: Using `core-shared-lib` in Other Projects

## 🎯 **Purpose**

This document provides **step-by-step instructions** for AI agents and developers on how to integrate and use the `core-shared-lib` project in other Java Spring Boot microservices. Follow these directions to ensure proper integration, configuration, and usage of the shared library components.

---

## 📋 **Prerequisites**

Before integrating `core-shared-lib`, ensure your target project has:

- ✅ **Java 17** or higher
- ✅ **Spring Boot 3.x** (recommended: 3.2.4+)
- ✅ **Maven** as build tool
- ✅ **Spring Security** dependency (for JWT functionality)
- ✅ **Spring Validation** dependency (for custom validators)

---

## 🚀 **Integration Steps**

### **Step 1: Add Dependency**

Add the `core-shared-lib` dependency to your project's `pom.xml`:

```xml
<dependency>
    <groupId>com.sharedlib</groupId>
    <artifactId>core-shared-lib</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### **Step 2: Configure Application Properties**

Add the following configuration to your `application.yml` or `application.properties`:

```yaml
# JWT Configuration
care:
  jwt:
    secret: ${JWT_SECRET:your-secret-key-here}
    expiration: ${JWT_EXPIRATION:86400000}  # 24 hours in milliseconds
    refreshExpiration: ${JWT_REFRESH_EXPIRATION:2592000000}  # 30 days
    issuer: care-platform

# Internationalization
spring:
  messages:
    basename: i18n/messages
    fallback-to-system-locale: false
    encoding: UTF-8
    cache-duration: 3600
  jackson:
    time-zone: UTC
    serialization:
      WRITE_DATES_AS_TIMESTAMPS: false

# Security Configuration
care:
  security:
    csrf:
      enabled: false
    cors:
      allowed-origins: "https://yourdomain.com"
      allowed-methods: "GET,POST,PUT,DELETE,OPTIONS"
      allowed-headers: "Authorization,Content-Type,X-Requested-With"
```

### **Step 3: Configure Spring Security**

Create or update your security configuration to include the JWT filter:

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtTokenProvider tokenProvider) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtAuthenticationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("https://yourdomain.com"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

### **Step 4: Create i18n Message Files**

Create message files for internationalization in `src/main/resources/i18n/`:

**`messages_en.properties`:**
```properties
# Error Messages
error.notfound=Resource not found
error.badrequest=Invalid request data
error.unauthorized=Unauthorized access
error.conflict=Resource conflict
error.validation=Validation failed
error.internal=Internal server error

# User Messages
user.email.already.exists=Email address is already registered
user.notfound=User not found with ID: {0}
user.invalid.credentials=Invalid email or password

# Success Messages
user.created=User created successfully
user.updated=User updated successfully
user.deleted=User deleted successfully
```

**`messages_ar.properties`:**
```properties
# Error Messages
error.notfound=المورد غير موجود
error.badrequest=بيانات الطلب غير صحيحة
error.unauthorized=وصول غير مصرح
error.conflict=تعارض في المورد
error.validation=فشل في التحقق من صحة البيانات
error.internal=خطأ داخلي في الخادم

# User Messages
user.email.already.exists=عنوان البريد الإلكتروني مسجل بالفعل
user.notfound=المستخدم غير موجود بالمعرف: {0}
user.invalid.credentials=البريد الإلكتروني أو كلمة المرور غير صحيحة

# Success Messages
user.created=تم إنشاء المستخدم بنجاح
user.updated=تم تحديث المستخدم بنجاح
user.deleted=تم حذف المستخدم بنجاح
```

---

## 🔧 **Usage Examples**

### **1. Using JWT Token Provider**

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @GetMapping("/profile")
    public ResponseEntity<UserInfoDto> getProfile(@RequestHeader("Authorization") String authHeader) {
        // Extract token from Authorization header
        String token = authHeader.replace("Bearer ", "");
        
        // Validate token
        if (!jwtTokenProvider.validateToken(token)) {
            throw new UnauthorizedException("Invalid token");
        }
        
        // Extract user information
        UUID userId = jwtTokenProvider.getUserId(token);
        String email = jwtTokenProvider.getEmail(token);
        String language = jwtTokenProvider.getLanguage(token);
        List<String> roles = jwtTokenProvider.getRoles(token);
        
        return ResponseEntity.ok(new UserInfoDto(userId, email, language, roles));
    }
}
```

### **2. Using Current User Context**

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @GetMapping("/me")
    public ResponseEntity<UserInfoDto> getCurrentUser() {
        // Get current user from context (automatically set by JWT filter)
        CurrentUser currentUser = CurrentUserContext.get();
        
        if (currentUser == null) {
            throw new UnauthorizedException("No authenticated user found");
        }
        
        return ResponseEntity.ok(new UserInfoDto(
            currentUser.getUserId(),
            currentUser.getEmail(),
            currentUser.getLanguage(),
            currentUser.getRoles()
        ));
    }
}
```

### **3. Using Language Context**

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @GetMapping("/preferences")
    public ResponseEntity<Map<String, String>> getUserPreferences() {
        // Get current language from context
        String currentLanguage = LanguageContext.getLanguage();
        
        Map<String, String> preferences = new HashMap<>();
        preferences.put("language", currentLanguage);
        preferences.put("timezone", "UTC");
        
        return ResponseEntity.ok(preferences);
    }
    
    @PutMapping("/language")
    public ResponseEntity<Void> updateLanguage(@RequestParam String language) {
        // Set language for current request
        LanguageContext.setLanguage(language);
        
        // Your logic to update user's language preference
        // ...
        
        return ResponseEntity.ok().build();
    }
}
```

### **4. Using Custom Validation Annotations**

```java
public class CreateUserRequest {
    
    @NotBlank(message = "Email is required")
    @ValidEmail
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    
    @NotBlank(message = "Full name is required")
    private String fullName;
    
    @SupportedLang
    private String language = "en";
    
    @ValidEnum(enumClass = UserStatus.class)
    private String status = "ACTIVE";
    
    @ValidPhone
    private String phoneNumber;
    
    // Getters and setters
}

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserRequest request) {
        // Validation is automatically performed by Spring
        // If validation fails, BadRequestException is thrown with localized messages
        
        // Your business logic here
        UserDto createdUser = userService.createUser(request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
}
```

### **5. Using Custom Exceptions**

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable @ValidUUID String userId) {
        UserDto user = userService.findById(UUID.fromString(userId))
            .orElseThrow(() -> new NotFoundException("user.notfound", userId));
        
        return ResponseEntity.ok(user);
    }
    
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserRequest request) {
        // Check if user already exists
        if (userService.existsByEmail(request.getEmail())) {
            throw new ConflictException("user.email.already.exists");
        }
        
        UserDto createdUser = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
    
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable @ValidUUID String userId,
            @Valid @RequestBody UpdateUserRequest request) {
        
        UserDto updatedUser = userService.updateUser(UUID.fromString(userId), request);
        return ResponseEntity.ok(updatedUser);
    }
    
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable @ValidUUID String userId) {
        if (!userService.existsById(UUID.fromString(userId))) {
            throw new NotFoundException("user.notfound", userId);
        }
        
        userService.deleteUser(UUID.fromString(userId));
        return ResponseEntity.noContent().build();
    }
}
```

### **6. Using DTOs**

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @GetMapping
    public ResponseEntity<List<UserInfoDto>> getAllUsers() {
        List<User> users = userService.findAll();
        
        List<UserInfoDto> userDtos = users.stream()
            .map(user -> new UserInfoDto(
                user.getId(),
                user.getEmail(),
                user.getLanguage(),
                user.getRoles()
            ))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(userDtos);
    }
    
    @GetMapping("/branches")
    public ResponseEntity<List<BranchInfoDto>> getBranches() {
        List<Branch> branches = branchService.findAll();
        
        List<BranchInfoDto> branchDtos = branches.stream()
            .map(branch -> new BranchInfoDto(
                branch.getId(),
                branch.getName(),
                branch.getCode(),
                branch.getAddress()
            ))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(branchDtos);
    }
}
```

---

## 🔍 **Testing Integration**

### **1. Test JWT Authentication**

```java
@SpringBootTest
@AutoConfigureTestDatabase
class UserControllerTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void shouldReturnUserProfile_WhenValidTokenProvided() {
        // Given
        String validToken = generateValidJwtToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(validToken);
        
        // When
        ResponseEntity<UserInfoDto> response = restTemplate.exchange(
            "/api/users/profile",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            UserInfoDto.class
        );
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test@example.com", response.getBody().email());
    }
    
    @Test
    void shouldReturnUnauthorized_WhenInvalidTokenProvided() {
        // Given
        String invalidToken = "invalid.token.here";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(invalidToken);
        
        // When
        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
            "/api/users/profile",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            ErrorResponse.class
        );
        
        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
```

### **2. Test Validation**

```java
@SpringBootTest
class CreateUserRequestTest {
    
    @Autowired
    private Validator validator;
    
    @Test
    void shouldPassValidation_WhenValidDataProvided() {
        // Given
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setFullName("John Doe");
        request.setLanguage("en");
        request.setStatus("ACTIVE");
        
        // When
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        
        // Then
        assertTrue(violations.isEmpty(), "Should have no validation violations");
    }
    
    @Test
    void shouldFailValidation_WhenInvalidEmailProvided() {
        // Given
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("invalid-email");
        request.setPassword("password123");
        request.setFullName("John Doe");
        
        // When
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        
        // Then
        assertFalse(violations.isEmpty(), "Should have validation violations");
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }
}
```

---

## 🚨 **Common Issues and Solutions**

### **Issue 1: JWT Token Not Being Processed**

**Problem:** JWT filter is not processing tokens from requests.

**Solution:** Ensure the JWT filter is properly configured in your security chain:

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http, JwtTokenProvider tokenProvider) throws Exception {
    http
        .addFilterBefore(new JwtAuthenticationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);
    // ... other configuration
    return http.build();
}
```

### **Issue 2: Validation Annotations Not Working**

**Problem:** Custom validation annotations are not being processed.

**Solution:** Ensure you have the validation dependency and proper component scanning:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

### **Issue 3: i18n Messages Not Loading**

**Problem:** Internationalized messages are not being resolved.

**Solution:** Check your message source configuration and file locations:

```yaml
spring:
  messages:
    basename: i18n/messages
    encoding: UTF-8
```

### **Issue 4: CurrentUser Context is Null**

**Problem:** `CurrentUserContext.get()` returns null.

**Solution:** Ensure the JWT filter is properly setting the user context and the request includes a valid JWT token.

---

## 📚 **Best Practices**

### **1. Security**
- Always use environment variables for JWT secrets
- Regularly rotate JWT secrets
- Implement proper CORS configuration
- Use HTTPS in production
- Validate all inputs using the provided validation annotations

### **2. Error Handling**
- Use the provided custom exceptions for consistent error responses
- Leverage i18n for user-friendly error messages
- Log errors appropriately for debugging
- Return appropriate HTTP status codes

### **3. Performance**
- JWT tokens are automatically cached for validation
- ThreadLocal contexts are automatically cleaned up
- Message resolution is cached for 1 hour
- Use appropriate validation annotations to catch errors early

### **4. Testing**
- Write comprehensive tests for all endpoints
- Test both positive and negative scenarios
- Use the provided test utilities and configurations
- Mock external dependencies appropriately

---

## 🔄 **Migration Guide**

### **From Manual JWT Implementation**

If you're migrating from a manual JWT implementation:

1. **Remove existing JWT code** from your controllers
2. **Add the core-shared-lib dependency**
3. **Configure the JWT filter** in your security configuration
4. **Update your controllers** to use `CurrentUserContext.get()`
5. **Replace manual validation** with the provided annotations
6. **Update error handling** to use the provided exceptions

### **From Different Validation Framework**

If you're migrating from a different validation framework:

1. **Replace existing validation annotations** with the provided ones
2. **Update validation messages** to use i18n keys
3. **Test all validation scenarios** to ensure compatibility
4. **Update documentation** to reflect the new validation approach

---

## 📞 **Support and Troubleshooting**

### **Getting Help**
- Check the `README.md` for detailed documentation
- Review the test examples in the library
- Check the `TEST_SUMMARY.md` files for usage patterns
- Create an issue in the project repository

### **Debugging Tips**
- Enable debug logging for JWT processing
- Check JWT token format and claims
- Verify message source configuration
- Test validation annotations individually

---

## 🎯 **Quick Reference**

### **Key Classes to Import**
```java
import com.sharedlib.core.security.JwtTokenProvider;
import com.sharedlib.core.context.CurrentUser;
import com.sharedlib.core.context.CurrentUserContext;
import com.sharedlib.core.context.LanguageContext;
import com.sharedlib.core.exception.*;
import com.sharedlib.core.dto.*;
import com.sharedlib.core.validation.*;
```

### **Common Annotations**
```java
@ValidUUID                    // Validates UUID format
@ValidEnum(enumClass = X.class) // Validates enum values
@SupportedLang               // Validates supported languages
@ValidEmail                  // Validates email format
@ValidPhone                  // Validates phone numbers
@ValidDateRange              // Validates date ranges
```

### **Common Exceptions**
```java
new NotFoundException("resource.notfound", id);
new BadRequestException("invalid.input");
new UnauthorizedException("access.denied");
new ConflictException("resource.conflict");
new MessageResolvableException("custom.message", args);
```

---

*This direction document ensures consistent and proper usage of the `core-shared-lib` across all microservices in the CARE Platform ecosystem.* 
package com.sharedlib.core.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link JwtTokenProvider} class.
 * Tests cover token generation, validation, claim extraction, and edge cases.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
@TestPropertySource(properties = {
    "jwt.secret=TestSecretKeyForJwtTokenProviderUnitTestsThatIsLongEnoughForHS512Algorithm1234567890",
    "jwt.expiration=3600000"
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private UUID testUserId;
    private String testEmail;
    private String testUserType;
    private String testLanguage;
    private List<String> testRoles;
    private List<String> testPermissions;
    private String validToken;

    @BeforeEach
    void setUp() {
        // Initialize test data
        testUserId = UUID.randomUUID();
        testEmail = "test@example.com";
        testUserType = "USER";
        testLanguage = "en";
        testRoles = Arrays.asList("USER", "ADMIN");
        testPermissions = Arrays.asList("READ", "WRITE", "DELETE");

        // Generate a valid token for testing
        validToken = jwtTokenProvider.generateToken(testUserId, testEmail, testUserType, 
                                                   testLanguage, testRoles, testPermissions);
    }

    @Test
    @Order(1)
    @DisplayName("Should generate valid token with all claims")
    void shouldGenerateValidTokenWithAllClaims() {
        // Given - test data is set up in setUp()

        // When
        String generatedToken = jwtTokenProvider.generateToken(testUserId, testEmail, testUserType, 
                                                             testLanguage, testRoles, testPermissions);

        // Then
        assertNotNull(generatedToken, "Generated token should not be null");
        assertTrue(generatedToken.length() > 0, "Generated token should not be empty");
        assertTrue(jwtTokenProvider.validateToken(generatedToken), "Generated token should be valid");
        
        // Verify all claims are correctly set
        assertEquals(testUserId, jwtTokenProvider.getUserId(generatedToken), "User ID should match");
        assertEquals(testEmail, jwtTokenProvider.getEmail(generatedToken), "Email should match");
        assertEquals(testUserType, jwtTokenProvider.getUserType(generatedToken), "User type should match");
        assertEquals(testLanguage, jwtTokenProvider.getLanguage(generatedToken), "Language should match");
        assertEquals(testRoles, jwtTokenProvider.getRoles(generatedToken), "Roles should match");
        assertEquals(testPermissions, jwtTokenProvider.getPermissions(generatedToken), "Permissions should match");
    }

    @Test
    @Order(2)
    @DisplayName("Should generate valid token with minimal claims")
    void shouldGenerateValidTokenWithMinimalClaims() {
        // Given
        UUID userId = UUID.randomUUID();
        String email = "minimal@example.com";

        // When
        String generatedToken = jwtTokenProvider.generateToken(userId, email, null, null, null, null);

        // Then
        assertNotNull(generatedToken, "Generated token should not be null");
        assertTrue(jwtTokenProvider.validateToken(generatedToken), "Generated token should be valid");
        
        // Verify claims
        assertEquals(userId, jwtTokenProvider.getUserId(generatedToken), "User ID should match");
        assertEquals(email, jwtTokenProvider.getEmail(generatedToken), "Email should match");
        assertNull(jwtTokenProvider.getUserType(generatedToken), "User type should be null");
        assertNull(jwtTokenProvider.getLanguage(generatedToken), "Language should be null");
        assertTrue(jwtTokenProvider.getRoles(generatedToken).isEmpty(), "Roles should be empty");
        assertTrue(jwtTokenProvider.getPermissions(generatedToken).isEmpty(), "Permissions should be empty");
    }

    @Test
    @Order(3)
    @DisplayName("Should validate valid token correctly")
    void shouldValidateValidTokenCorrectly() {
        // Given - validToken is generated in setUp()

        // When
        boolean isValid = jwtTokenProvider.validateToken(validToken);

        // Then
        assertTrue(isValid, "Valid token should return true");
    }

    @Test
    @Order(4)
    @DisplayName("Should return false for null token")
    void shouldReturnFalseForNullToken() {
        // Given
        String nullToken = null;

        // When
        boolean isValid = jwtTokenProvider.validateToken(nullToken);

        // Then
        assertFalse(isValid, "Null token should return false");
    }

    @Test
    @Order(5)
    @DisplayName("Should return false for empty token")
    void shouldReturnFalseForEmptyToken() {
        // Given
        String emptyToken = "";

        // When
        boolean isValid = jwtTokenProvider.validateToken(emptyToken);

        // Then
        assertFalse(isValid, "Empty token should return false");
    }

    @Test
    @Order(6)
    @DisplayName("Should return false for malformed token")
    void shouldReturnFalseForMalformedToken() {
        // Given
        String malformedToken = "not.a.valid.jwt.token";

        // When
        boolean isValid = jwtTokenProvider.validateToken(malformedToken);

        // Then
        assertFalse(isValid, "Malformed token should return false");
    }

    @Test
    @Order(7)
    @DisplayName("Should return false for token with wrong signature")
    void shouldReturnFalseForTokenWithWrongSignature() {
        // Given
        String wrongSignatureToken = generateTokenWithWrongSecret();

        // When
        boolean isValid = jwtTokenProvider.validateToken(wrongSignatureToken);

        // Then
        assertFalse(isValid, "Token with wrong signature should return false");
    }

    @Test
    @Order(8)
    @DisplayName("Should extract user ID correctly")
    void shouldExtractUserIdCorrectly() {
        // Given - validToken is generated in setUp()

        // When
        UUID extractedUserId = jwtTokenProvider.getUserId(validToken);

        // Then
        assertEquals(testUserId, extractedUserId, "Extracted user ID should match the original");
    }

    @Test
    @Order(9)
    @DisplayName("Should extract email correctly")
    void shouldExtractEmailCorrectly() {
        // Given - validToken is generated in setUp()

        // When
        String extractedEmail = jwtTokenProvider.getEmail(validToken);

        // Then
        assertEquals(testEmail, extractedEmail, "Extracted email should match the original");
    }

    @Test
    @Order(10)
    @DisplayName("Should extract user type correctly")
    void shouldExtractUserTypeCorrectly() {
        // Given - validToken is generated in setUp()

        // When
        String extractedUserType = jwtTokenProvider.getUserType(validToken);

        // Then
        assertEquals(testUserType, extractedUserType, "Extracted user type should match the original");
    }

    @Test
    @Order(11)
    @DisplayName("Should extract language correctly")
    void shouldExtractLanguageCorrectly() {
        // Given - validToken is generated in setUp()

        // When
        String extractedLanguage = jwtTokenProvider.getLanguage(validToken);

        // Then
        assertEquals(testLanguage, extractedLanguage, "Extracted language should match the original");
    }

    @Test
    @Order(12)
    @DisplayName("Should extract roles as List correctly")
    void shouldExtractRolesAsListCorrectly() {
        // Given - validToken is generated in setUp()

        // When
        List<String> extractedRoles = jwtTokenProvider.getRoles(validToken);

        // Then
        assertEquals(testRoles, extractedRoles, "Extracted roles should match the original");
    }

    @Test
    @Order(13)
    @DisplayName("Should extract roles as comma-separated string correctly")
    void shouldExtractRolesAsCommaSeparatedStringCorrectly() {
        // Given
        String rolesString = "USER,ADMIN,MANAGER";
        String tokenWithStringRoles = generateTokenWithStringRoles(rolesString);

        // When
        List<String> extractedRoles = jwtTokenProvider.getRoles(tokenWithStringRoles);

        // Then
        List<String> expectedRoles = Arrays.asList("USER", "ADMIN", "MANAGER");
        assertEquals(expectedRoles, extractedRoles, "Extracted roles from string should match expected");
    }

    @Test
    @Order(14)
    @DisplayName("Should extract permissions as List correctly")
    void shouldExtractPermissionsAsListCorrectly() {
        // Given - validToken is generated in setUp()

        // When
        List<String> extractedPermissions = jwtTokenProvider.getPermissions(validToken);

        // Then
        assertEquals(testPermissions, extractedPermissions, "Extracted permissions should match the original");
    }

    @Test
    @Order(15)
    @DisplayName("Should extract permissions as comma-separated string correctly")
    void shouldExtractPermissionsAsCommaSeparatedStringCorrectly() {
        // Given
        String permissionsString = "READ,WRITE,DELETE,UPDATE";
        String tokenWithStringPermissions = generateTokenWithStringPermissions(permissionsString);

        // When
        List<String> extractedPermissions = jwtTokenProvider.getPermissions(tokenWithStringPermissions);

        // Then
        List<String> expectedPermissions = Arrays.asList("READ", "WRITE", "DELETE", "UPDATE");
        assertEquals(expectedPermissions, extractedPermissions, "Extracted permissions from string should match expected");
    }

    @Test
    @Order(16)
    @DisplayName("Should return empty list for missing roles")
    void shouldReturnEmptyListForMissingRoles() {
        // Given
        String tokenWithoutRoles = jwtTokenProvider.generateToken(testUserId, testEmail, testUserType, 
                                                                 testLanguage, null, testPermissions);

        // When
        List<String> extractedRoles = jwtTokenProvider.getRoles(tokenWithoutRoles);

        // Then
        assertTrue(extractedRoles.isEmpty(), "Missing roles should return empty list");
    }

    @Test
    @Order(17)
    @DisplayName("Should return empty list for missing permissions")
    void shouldReturnEmptyListForMissingPermissions() {
        // Given
        String tokenWithoutPermissions = jwtTokenProvider.generateToken(testUserId, testEmail, testUserType, 
                                                                       testLanguage, testRoles, null);

        // When
        List<String> extractedPermissions = jwtTokenProvider.getPermissions(tokenWithoutPermissions);

        // Then
        assertTrue(extractedPermissions.isEmpty(), "Missing permissions should return empty list");
    }

    @Test
    @Order(18)
    @DisplayName("Should extract expiration date correctly")
    void shouldExtractExpirationDateCorrectly() {
        // Given - validToken is generated in setUp()

        // When
        Date expirationDate = jwtTokenProvider.getExpirationDate(validToken);

        // Then
        assertNotNull(expirationDate, "Expiration date should not be null");
        assertTrue(expirationDate.after(new Date()), "Expiration date should be in the future");
    }

    @Test
    @Order(19)
    @DisplayName("Should handle expired token correctly")
    void shouldHandleExpiredTokenCorrectly() {
        // Given
        String expiredToken = generateExpiredToken();

        // When
        boolean isValid = jwtTokenProvider.validateToken(expiredToken);

        // Then
        assertFalse(isValid, "Expired token should return false");
    }

    @Test
    @Order(20)
    @DisplayName("Should handle token with invalid UUID subject")
    void shouldHandleTokenWithInvalidUuidSubject() {
        // Given
        String tokenWithInvalidUuid = generateTokenWithInvalidUuid();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            jwtTokenProvider.getUserId(tokenWithInvalidUuid);
        }, "Should throw IllegalArgumentException for invalid UUID");
    }

    @Test
    @Order(21)
    @DisplayName("Should handle token with null claims gracefully")
    void shouldHandleTokenWithNullClaimsGracefully() {
        // Given
        String tokenWithNullClaims = generateTokenWithNullClaims();

        // When
        String extractedEmail = jwtTokenProvider.getEmail(tokenWithNullClaims);
        String extractedUserType = jwtTokenProvider.getUserType(tokenWithNullClaims);
        String extractedLanguage = jwtTokenProvider.getLanguage(tokenWithNullClaims);

        // Then
        assertNull(extractedEmail, "Null email claim should return null");
        assertNull(extractedUserType, "Null user type claim should return null");
        assertNull(extractedLanguage, "Null language claim should return null");
    }

    @Test
    @Order(22)
    @DisplayName("Should handle special characters in claims")
    void shouldHandleSpecialCharactersInClaims() {
        // Given
        String emailWithSpecialChars = "test+user@example.com";
        String userTypeWithSpecialChars = "ADMIN_USER";
        String languageWithSpecialChars = "en-US";
        List<String> rolesWithSpecialChars = Arrays.asList("ADMIN_USER", "SUPER_ADMIN");
        
        String tokenWithSpecialChars = jwtTokenProvider.generateToken(testUserId, emailWithSpecialChars, 
                                                                     userTypeWithSpecialChars, languageWithSpecialChars, 
                                                                     rolesWithSpecialChars, testPermissions);

        // When
        String extractedEmail = jwtTokenProvider.getEmail(tokenWithSpecialChars);
        String extractedUserType = jwtTokenProvider.getUserType(tokenWithSpecialChars);
        String extractedLanguage = jwtTokenProvider.getLanguage(tokenWithSpecialChars);
        List<String> extractedRoles = jwtTokenProvider.getRoles(tokenWithSpecialChars);

        // Then
        assertEquals(emailWithSpecialChars, extractedEmail, "Email with special characters should be preserved");
        assertEquals(userTypeWithSpecialChars, extractedUserType, "User type with special characters should be preserved");
        assertEquals(languageWithSpecialChars, extractedLanguage, "Language with special characters should be preserved");
        assertEquals(rolesWithSpecialChars, extractedRoles, "Roles with special characters should be preserved");
    }

    // Helper methods for generating test tokens

    private String generateTokenWithWrongSecret() {
        String wrongSecret = "WrongSecretKeyForTestingPurposesOnly1234567890";
        return Jwts.builder()
                .subject(testUserId.toString())
                .claim("email", testEmail)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(Keys.hmacShaKeyFor(wrongSecret.getBytes()))
                .compact();
    }

    private String generateTokenWithStringRoles(String rolesString) {
        return Jwts.builder()
                .subject(testUserId.toString())
                .claim("email", testEmail)
                .claim("roles", rolesString)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(Keys.hmacShaKeyFor("TestSecretKeyForJwtTokenProviderUnitTestsThatIsLongEnoughForHS512Algorithm1234567890".getBytes()))
                .compact();
    }

    private String generateTokenWithStringPermissions(String permissionsString) {
        return Jwts.builder()
                .subject(testUserId.toString())
                .claim("email", testEmail)
                .claim("permissions", permissionsString)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(Keys.hmacShaKeyFor("TestSecretKeyForJwtTokenProviderUnitTestsThatIsLongEnoughForHS512Algorithm1234567890".getBytes()))
                .compact();
    }

    private String generateExpiredToken() {
        return Jwts.builder()
                .subject(testUserId.toString())
                .claim("email", testEmail)
                .issuedAt(new Date(System.currentTimeMillis() - 7200000)) // 2 hours ago
                .expiration(new Date(System.currentTimeMillis() - 3600000)) // 1 hour ago
                .signWith(Keys.hmacShaKeyFor("TestSecretKeyForJwtTokenProviderUnitTestsThatIsLongEnoughForHS512Algorithm1234567890".getBytes()))
                .compact();
    }

    private String generateTokenWithInvalidUuid() {
        return Jwts.builder()
                .subject("invalid-uuid-format")
                .claim("email", testEmail)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(Keys.hmacShaKeyFor("TestSecretKeyForJwtTokenProviderUnitTestsThatIsLongEnoughForHS512Algorithm1234567890".getBytes()))
                .compact();
    }

    private String generateTokenWithNullClaims() {
        return Jwts.builder()
                .subject(testUserId.toString())
                .claim("email", null)
                .claim("userType", null)
                .claim("lang", null)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(Keys.hmacShaKeyFor("TestSecretKeyForJwtTokenProviderUnitTestsThatIsLongEnoughForHS512Algorithm1234567890".getBytes()))
                .compact();
    }
} 
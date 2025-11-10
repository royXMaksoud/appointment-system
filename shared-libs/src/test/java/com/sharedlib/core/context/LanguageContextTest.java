package com.sharedlib.core.context;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link LanguageContext} class.
 * Tests cover language setting, retrieval, fallback behavior, and thread safety.
 */
@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LanguageContextTest {

    @BeforeEach
    void setUp() {
        // Clear any existing language context before each test
        LanguageContext.clear();
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test to prevent interference
        LanguageContext.clear();
    }

    @Test
    @Order(1)
    @DisplayName("Should set and retrieve language correctly")
    void shouldSetAndRetrieveLanguageCorrectly() {
        // Given
        String expectedLanguage = "ar";

        // When
        LanguageContext.setLanguage(expectedLanguage);
        String actualLanguage = LanguageContext.getLanguage();

        // Then
        assertEquals(expectedLanguage, actualLanguage, "Language should be set and retrieved correctly");
    }

    @Test
    @Order(2)
    @DisplayName("Should return default language 'en' when no language is set")
    void shouldReturnDefaultLanguageWhenNoLanguageIsSet() {
        // Given - no language set (cleared in setUp)

        // When
        String actualLanguage = LanguageContext.getLanguage();

        // Then
        assertEquals("en", actualLanguage, "Should return default language 'en' when no language is set");
    }

    @Test
    @Order(3)
    @DisplayName("Should return default language 'en' when null language is set")
    void shouldReturnDefaultLanguageWhenNullLanguageIsSet() {
        // Given
        LanguageContext.setLanguage(null);

        // When
        String actualLanguage = LanguageContext.getLanguage();

        // Then
        assertEquals("en", actualLanguage, "Should return default language 'en' when null language is set");
    }

    @Test
    @Order(4)
    @DisplayName("Should return empty string when empty language is set")
    void shouldReturnEmptyStringWhenEmptyLanguageIsSet() {
        // Given
        LanguageContext.setLanguage("");

        // When
        String actualLanguage = LanguageContext.getLanguage();

        // Then
        assertEquals("", actualLanguage, "Should return empty string when empty language is set");
    }

    @Test
    @Order(5)
    @DisplayName("Should return blank string when blank language is set")
    void shouldReturnBlankStringWhenBlankLanguageIsSet() {
        // Given
        LanguageContext.setLanguage("   ");

        // When
        String actualLanguage = LanguageContext.getLanguage();

        // Then
        assertEquals("   ", actualLanguage, "Should return blank string when blank language is set");
    }

    @Test
    @Order(6)
    @DisplayName("Should clear language context correctly")
    void shouldClearLanguageContextCorrectly() {
        // Given
        LanguageContext.setLanguage("fr");

        // When
        LanguageContext.clear();
        String actualLanguage = LanguageContext.getLanguage();

        // Then
        assertEquals("en", actualLanguage, "Should return default language after clearing context");
    }

    @Test
    @Order(7)
    @DisplayName("Should handle multiple language changes correctly")
    void shouldHandleMultipleLanguageChangesCorrectly() {
        // Given
        String firstLanguage = "es";
        String secondLanguage = "de";
        String thirdLanguage = "it";

        // When
        LanguageContext.setLanguage(firstLanguage);
        String firstResult = LanguageContext.getLanguage();

        LanguageContext.setLanguage(secondLanguage);
        String secondResult = LanguageContext.getLanguage();

        LanguageContext.setLanguage(thirdLanguage);
        String thirdResult = LanguageContext.getLanguage();

        // Then
        assertEquals(firstLanguage, firstResult, "First language should be set correctly");
        assertEquals(secondLanguage, secondResult, "Second language should override first language");
        assertEquals(thirdLanguage, thirdResult, "Third language should override second language");
    }

    @Test
    @Order(8)
    @DisplayName("Should handle case-sensitive language codes")
    void shouldHandleCaseSensitiveLanguageCodes() {
        // Given
        String upperCaseLanguage = "EN";
        String lowerCaseLanguage = "en";
        String mixedCaseLanguage = "En";

        // When
        LanguageContext.setLanguage(upperCaseLanguage);
        String upperCaseResult = LanguageContext.getLanguage();

        LanguageContext.setLanguage(lowerCaseLanguage);
        String lowerCaseResult = LanguageContext.getLanguage();

        LanguageContext.setLanguage(mixedCaseLanguage);
        String mixedCaseResult = LanguageContext.getLanguage();

        // Then
        assertEquals(upperCaseLanguage, upperCaseResult, "UpperCase language should be preserved");
        assertEquals(lowerCaseLanguage, lowerCaseResult, "LowerCase language should be preserved");
        assertEquals(mixedCaseLanguage, mixedCaseResult, "MixedCase language should be preserved");
    }

    @Test
    @Order(9)
    @DisplayName("Should handle special characters in language codes")
    void shouldHandleSpecialCharactersInLanguageCodes() {
        // Given
        String languageWithHyphen = "en-US";
        String languageWithUnderscore = "en_GB";
        String languageWithNumbers = "zh-CN";

        // When
        LanguageContext.setLanguage(languageWithHyphen);
        String hyphenResult = LanguageContext.getLanguage();

        LanguageContext.setLanguage(languageWithUnderscore);
        String underscoreResult = LanguageContext.getLanguage();

        LanguageContext.setLanguage(languageWithNumbers);
        String numbersResult = LanguageContext.getLanguage();

        // Then
        assertEquals(languageWithHyphen, hyphenResult, "Language with hyphen should be preserved");
        assertEquals(languageWithUnderscore, underscoreResult, "Language with underscore should be preserved");
        assertEquals(languageWithNumbers, numbersResult, "Language with numbers should be preserved");
    }

    @Test
    @Order(10)
    @DisplayName("Should be thread-safe for concurrent access")
    void shouldBeThreadSafeForConcurrentAccess() throws InterruptedException {
        // Given
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicReference<Throwable> exception = new AtomicReference<>();

        // When
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    String language = "lang-" + threadId;
                    LanguageContext.setLanguage(language);
                    Thread.sleep(10); // Simulate some work
                    String retrievedLanguage = LanguageContext.getLanguage();
                    
                    if (!language.equals(retrievedLanguage)) {
                        exception.set(new AssertionError(
                            String.format("Thread %d: Expected %s but got %s", 
                                threadId, language, retrievedLanguage)));
                    }
                } catch (Exception e) {
                    exception.set(e);
                } finally {
                    LanguageContext.clear();
                    latch.countDown();
                }
            });
        }

        // Then
        latch.await();
        executor.shutdown();

        if (exception.get() != null) {
            fail("Thread safety test failed: " + exception.get().getMessage());
        }
    }

    @Test
    @Order(11)
    @DisplayName("Should handle very long language codes")
    void shouldHandleVeryLongLanguageCodes() {
        // Given
        String longLanguageCode = "very-long-language-code-that-exceeds-normal-length";

        // When
        LanguageContext.setLanguage(longLanguageCode);
        String actualLanguage = LanguageContext.getLanguage();

        // Then
        assertEquals(longLanguageCode, actualLanguage, "Very long language code should be handled correctly");
    }

    @Test
    @Order(12)
    @DisplayName("Should handle unicode characters in language codes")
    void shouldHandleUnicodeCharactersInLanguageCodes() {
        // Given
        String unicodeLanguage = "zh-中文";

        // When
        LanguageContext.setLanguage(unicodeLanguage);
        String actualLanguage = LanguageContext.getLanguage();

        // Then
        assertEquals(unicodeLanguage, actualLanguage, "Unicode characters in language code should be preserved");
    }

    @Test
    @Order(13)
    @DisplayName("Should return default language after clearing multiple times")
    void shouldReturnDefaultLanguageAfterClearingMultipleTimes() {
        // Given
        LanguageContext.setLanguage("fr");

        // When
        LanguageContext.clear();
        LanguageContext.clear(); // Clear again
        LanguageContext.clear(); // Clear one more time
        String actualLanguage = LanguageContext.getLanguage();

        // Then
        assertEquals("en", actualLanguage, "Should return default language after multiple clears");
    }

    @Test
    @Order(14)
    @DisplayName("Should handle setting same language multiple times")
    void shouldHandleSettingSameLanguageMultipleTimes() {
        // Given
        String language = "pt";

        // When
        LanguageContext.setLanguage(language);
        LanguageContext.setLanguage(language);
        LanguageContext.setLanguage(language);
        String actualLanguage = LanguageContext.getLanguage();

        // Then
        assertEquals(language, actualLanguage, "Should handle setting same language multiple times correctly");
    }

    @Test
    @Order(15)
    @DisplayName("Should handle rapid language changes")
    void shouldHandleRapidLanguageChanges() {
        // Given
        String[] languages = {"en", "ar", "fr", "es", "de", "it", "pt", "ru", "ja", "ko"};

        // When & Then
        for (String language : languages) {
            LanguageContext.setLanguage(language);
            String actualLanguage = LanguageContext.getLanguage();
            assertEquals(language, actualLanguage, 
                String.format("Should handle rapid change to language: %s", language));
        }
    }
} 
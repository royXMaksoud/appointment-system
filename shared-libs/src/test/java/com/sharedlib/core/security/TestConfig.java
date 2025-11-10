package com.sharedlib.core.security;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * Test configuration for JWT token provider tests.
 */
@TestConfiguration
@ComponentScan(basePackages = "com.sharedlib.core.security")
public class TestConfig {
    
    // The JwtTokenProvider will be automatically scanned and configured
    // with the test properties from @TestPropertySource
} 
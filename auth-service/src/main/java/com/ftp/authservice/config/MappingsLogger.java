// src/main/java/com/ftp/authservice/config/MappingsLogger.java
package com.ftp.authservice.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
public class MappingsLogger {

    @Bean
    ApplicationRunner logMappings(
            @Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping mapping) {
        return args -> mapping.getHandlerMethods().forEach((info, method) -> {
            System.out.println("[MAPPING] " + info + " -> " + method);
        });
    }
}

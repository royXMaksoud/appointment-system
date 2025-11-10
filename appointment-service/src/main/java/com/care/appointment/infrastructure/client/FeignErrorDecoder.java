package com.care.appointment.infrastructure.client;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Custom error decoder for Feign clients
 */
@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {
    
    private final ErrorDecoder defaultErrorDecoder = new Default();
    
    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());
        
        log.error("Feign client error: method={}, status={}, reason={}", 
            methodKey, response.status(), response.reason());
        
        switch (status) {
            case NOT_FOUND:
                return new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Resource not found in access-management-service");
            case FORBIDDEN:
                return new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "Access denied to resource in access-management-service");
            case UNAUTHORIZED:
                return new ResponseStatusException(HttpStatus.UNAUTHORIZED, 
                    "Unauthorized access to access-management-service");
            case SERVICE_UNAVAILABLE:
                return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, 
                    "Access-management-service is unavailable");
            default:
                return defaultErrorDecoder.decode(methodKey, response);
        }
    }
}


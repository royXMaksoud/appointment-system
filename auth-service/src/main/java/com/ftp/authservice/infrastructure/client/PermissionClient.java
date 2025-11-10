package com.ftp.authservice.infrastructure.client;

import com.ftp.authservice.application.dto.permissions.PageResponse;
import com.ftp.authservice.application.dto.permissions.PermissionRow;
import com.ftp.authservice.infrastructure.config.PermissionServiceProperties;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PermissionClient {

    private static final Logger log = LoggerFactory.getLogger(PermissionClient.class);

    private final WebClient permissionWebClient;
    private final PermissionServiceProperties props;

    @CircuitBreaker(name = "permissionService", fallbackMethod = "fallbackFetchUserPermissions")
    @Retry(name = "permissionService")
    public PageResponse<PermissionRow> fetchUserPermissionsPage(
            UUID userId, Integer page, Integer size,
            String systemId, String actionCode, String scopeType) {

        String path = props.getUserPermissionsPath() + "/" + userId;
        String fullUrl = buildFullUrl(props.getBaseUrl(), path, page, size, systemId, actionCode, scopeType);

        Instant start = Instant.now();
        try {
            return permissionWebClient.get()
                    .uri(uri -> uri.path(path)
                            .queryParamIfPresent("page", Optional.ofNullable(page))
                            .queryParamIfPresent("size", Optional.ofNullable(size))
                            .queryParamIfPresent("systemId", Optional.ofNullable(systemId))
                            .queryParamIfPresent("actionCode", Optional.ofNullable(actionCode))
                            .queryParamIfPresent("scopeType", Optional.ofNullable(scopeType))
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchangeToMono(resp -> {
                        long tookMs = Duration.between(start, Instant.now()).toMillis();
                        if (resp.statusCode().is2xxSuccessful()) {
                            log.debug("PermissionService OK {} ({} ms)", fullUrl, tookMs);
                            return resp.bodyToMono(new ParameterizedTypeReference<PageResponse<PermissionRow>>() {});
                        }
                        return resp.bodyToMono(String.class).defaultIfEmpty("")
                                .flatMap(body -> {
                                    log.error("PermissionService HTTP {} {} ({} ms) body={}",
                                            resp.statusCode().value(), fullUrl, tookMs, body);
                                    return Mono.error(new ResponseStatusException(
                                            resp.statusCode(),
                                            "Permission service error at " + fullUrl + " -> " + body));
                                });
                    })
                    .timeout(Duration.ofSeconds(5))
                    .block();

        } catch (WebClientRequestException netEx) {
            String msg = String.format("Permission service network error (%s) at %s: %s" +path + " "+fullUrl,
                    netEx.getClass().getSimpleName(), fullUrl, netEx.getMessage());
            log.error(msg, netEx);
            throw new ResponseStatusException(org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE, msg, netEx);

        } catch (ResponseStatusException rse) {
            log.error("Permission service RSE at {} -> {} {}", fullUrl, rse.getStatusCode(), rse.getReason());
            throw rse;

        } catch (Exception ex) {
            String msg = "Permission service unexpected error at " + fullUrl + ": " + ex.getMessage();
            log.error(msg, ex);
            throw new ResponseStatusException(org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE, msg, ex);
        }
    }

    private static String buildFullUrl(String base, String path,
                                       Integer page, Integer size,
                                       String systemId, String actionCode, String scopeType) {
        StringBuilder q = new StringBuilder();
        if (page != null) q.append("page=").append(page);
        if (size != null) q.append(q.length()>0?"&":"").append("size=").append(size);
        if (systemId != null) q.append(q.length()>0?"&":"").append("systemId=").append(encode(systemId));
        if (actionCode != null) q.append(q.length()>0?"&":"").append("actionCode=").append(encode(actionCode));
        if (scopeType != null) q.append(q.length()>0?"&":"").append("scopeType=").append(encode(scopeType));

        String sep = base.endsWith("/") ? "" : "/";
        String url = base + sep + (path.startsWith("/") ? path.substring(1) : path);
        return q.length() > 0 ? url + "?" + q : url;
    }

    private static String encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    // Fallback method for Circuit Breaker
    private PageResponse<PermissionRow> fallbackFetchUserPermissions(
            UUID userId, Integer page, Integer size,
            String systemId, String actionCode, String scopeType,
            Exception ex) {
        
        log.warn("Fallback triggered for user {} permissions. Returning empty permissions. Error: {}", 
                userId, ex.getMessage());
        
        // Return empty permissions instead of failing
        return new PageResponse<>(
                java.util.Collections.emptyList(),  // content
                0L,                                  // totalElements
                0,                                   // totalPages
                page != null ? page : 0,            // number
                size != null ? size : 1000,         // size
                true,                                // last
                true                                 // first
        );
    }
}

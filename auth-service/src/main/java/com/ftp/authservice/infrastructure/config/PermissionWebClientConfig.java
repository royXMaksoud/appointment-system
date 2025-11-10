package com.ftp.authservice.infrastructure.config;

import io.netty.channel.ChannelOption;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(PermissionServiceProperties.class)
@RequiredArgsConstructor
public class PermissionWebClientConfig {

    private final PermissionServiceProperties props;

    @Value("${permission.internal-key}")
    private String internalApiKey;

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient permissionWebClient(WebClient.Builder webClientBuilder) {
        HttpClient httpClient = HttpClient.create()
                .wiretap(true)
                .responseTimeout(Duration.ofMillis(props.getReadTimeoutMs()))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, props.getConnectTimeoutMs());

        return webClientBuilder
                .baseUrl(props.getBaseUrl())
                .defaultHeader("X-Internal-Key", internalApiKey)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}

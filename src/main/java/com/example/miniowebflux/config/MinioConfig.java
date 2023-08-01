package com.example.miniowebflux.config;

import io.minio.MinioAsyncClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {
    @Bean
    public MinioAsyncClient minioClient(
        @Value("${minio.endpoint}") String endpoint,
        @Value("${minio.username}") String username,
        @Value("${minio.password}") String password
    ) {
        return MinioAsyncClient.builder()
            .endpoint(endpoint)
            .credentials(username, password)
            .build();
    }
}

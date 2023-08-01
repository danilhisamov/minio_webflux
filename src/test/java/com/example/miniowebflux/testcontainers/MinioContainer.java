package com.example.miniowebflux.testcontainers;

import java.time.Duration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;


public class MinioContainer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static final int DEFAULT_PORT = 9000;
    private static final String DEFAULT_IMAGE = "minio/minio";
    private static final String DEFAULT_TAG = "RELEASE.2023-07-18T17-49-40Z";
    private static final String DEFAULT_STORAGE_DIRECTORY = "/data";
    private static final String HEALTH_ENDPOINT = "/minio/health/ready";

    @Override
    public void initialize(ConfigurableApplicationContext ctx) {
        final var e = ctx.getEnvironment();
        final var container = new GenericContainer<>(DEFAULT_IMAGE + ":" + DEFAULT_TAG);

        container
            .withExposedPorts(DEFAULT_PORT)
            .withEnv("MINIO_ROOT_USER", e.getProperty("minio.username"))
            .withEnv("MINIO_ROOT_PASSWORD", e.getProperty("minio.password"))
            .withCommand("server", DEFAULT_STORAGE_DIRECTORY);

        container.setWaitStrategy(new HttpWaitStrategy()
            .forPort(DEFAULT_PORT)
            .forPath(HEALTH_ENDPOINT)
            .withStartupTimeout(Duration.ofMinutes(2)));

        container.start();

        // taking parameters of created container
        // and writing them into application properties
        TestPropertyValues.of(
                "minio.endpoint=" + String.format("http://%s:%s", container.getHost(), container.getMappedPort(DEFAULT_PORT))
            )
            .applyTo(ctx);

        // just in case: testcontainers provides Ryuk and JVM shutdown hook
        // to clean up unused containers
        ctx.addApplicationListener(event -> {
            if (event instanceof ContextClosedEvent) {
                container.stop();
            }
        });
    }
}

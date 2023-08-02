package com.example.miniowebflux.service;

import com.example.miniowebflux.TestMinioWebfluxApplication;
import com.example.miniowebflux.testcontainers.MinioContainer;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringJUnitConfig(
    initializers = {
        MinioContainer.class
    }
)
@SpringBootTest(
    classes = TestMinioWebfluxApplication.class
)
class MinioServiceTest {
    @Autowired
    private MinioService minioService;

    final String bucket = "my-bucket";
    final String resourceFilename = "video_cat_sonar.mp4";
    final Path resourceFilePath = Path.of(new ClassPathResource(resourceFilename).getURI());

    MinioServiceTest() throws IOException {
    }

    @RepeatedTest(20)
    void testSuccess() {
        final var object = UUID.randomUUID() + "-" + resourceFilename;

        StepVerifier.create(
                minioService
                    .isBucketExist(bucket)
                    .filter(Boolean.TRUE::equals) // if bucket doesn't exist Mono will be empty
                    .switchIfEmpty(minioService.createBucket(bucket).thenReturn(true))
                    .then(minioService.uploadObject(bucket, object, resourceFilePath))
                    .log()
            )
            .expectNextMatches((owr) -> owr.bucket().equals(bucket) && owr.object().equals(object))
            .verifyComplete();

        StepVerifier.create(minioService.isBucketExist(bucket))
            .expectNext(true)
            .verifyComplete();

        StepVerifier.create(minioService.listObjects(bucket, true).count())
            .expectNext(1L)
            .verifyComplete();

        StepVerifier.create(minioService.removeObject(bucket, object))
            .verifyComplete();

        StepVerifier.create(minioService.removeBucket(bucket))
            .verifyComplete();

        StepVerifier.create(minioService.isBucketExist(bucket))
            .expectNext(false)
            .verifyComplete();
    }

    @RepeatedTest(20)
    void testFail() {
        final var object = UUID.randomUUID() + "-" + resourceFilename;

        StepVerifier.create(
                minioService
                    .isBucketExist(bucket)
                    .flatMap(exist -> {
                        if (Boolean.FALSE.equals(exist)) {
                            return minioService.createBucket(bucket);
                        }
                        return Mono.just(true).then();
                    })
                    .then(minioService.uploadObject(bucket, object, resourceFilePath))
            )
            .expectNextMatches(owr -> owr.bucket().equals(bucket) && owr.object().equals(object))
            .verifyComplete();

        StepVerifier.create(minioService.isBucketExist(bucket))
            .expectNext(true)
            .verifyComplete();

        StepVerifier.create(minioService.listObjects(bucket, true).count())
            .expectNext(1L)
            .verifyComplete();

        StepVerifier.create(minioService.removeObject(bucket, object))
            .verifyComplete();

        StepVerifier.create(minioService.removeBucket(bucket))
            .verifyComplete();

        StepVerifier.create(minioService.isBucketExist(bucket))
            .expectNext(false)
            .verifyComplete();
    }
}
package com.example.miniowebflux.service;

import io.minio.BucketExistsArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioAsyncClient;
import io.minio.ObjectWriteResponse;
import io.minio.RemoveBucketArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.UploadObjectArgs;
import io.minio.messages.Item;
import java.nio.file.Path;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MinioService {
    private final MinioAsyncClient minioClient;

    public MinioService(MinioAsyncClient minioClient) {
        this.minioClient = minioClient;
    }

    public Mono<Boolean> isBucketExist(String bucket) {
        try {
            return Mono.fromFuture(
                minioClient.bucketExists(
                    BucketExistsArgs.builder()
                        .bucket(bucket)
                        .build()
                )
            );
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    public Mono<Void> createBucket(String bucket) {
        try {
            return Mono.fromFuture(
                minioClient.makeBucket(
                    MakeBucketArgs
                        .builder()
                        .bucket(bucket)
                        .build()
                )
            );
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    public Mono<ObjectWriteResponse> uploadObject(String bucket, String object, Path path) {
        try {
            return Mono.fromFuture(
                minioClient.uploadObject(
                    UploadObjectArgs.builder()
                        .bucket(bucket)
                        .object(object)
                        .filename(path.toAbsolutePath().toString())
                        .build()
                )
            );
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    public Flux<Result<Item>> listObjects(String bucket, boolean recursive) {
        try {
            return Flux.fromIterable(
                minioClient.listObjects(
                    ListObjectsArgs.builder()
                        .bucket(bucket)
                        .recursive(recursive)
                        .build()
                )
            );
        } catch (Exception e) {
            return Flux.error(e);
        }
    }

    public Mono<Void> removeObject(String bucket, String object) {
        try {
            return Mono.fromFuture(
                minioClient.removeObject(
                    RemoveObjectArgs.builder()
                        .bucket(bucket)
                        .object(object)
                        .build()
                )
            );
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    public Mono<Void> removeBucket(String bucket) {
        try {
            return Mono.fromFuture(
                minioClient.removeBucket(
                    RemoveBucketArgs.builder()
                        .bucket(bucket)
                        .build()
                )
            );
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}

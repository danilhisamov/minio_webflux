package com.example.miniowebflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestMinioWebfluxApplication {

    public static void main(String[] args) {
        SpringApplication.from(MinioWebfluxApplication::main).with(TestMinioWebfluxApplication.class).run(args);
    }

}

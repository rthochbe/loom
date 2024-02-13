package com.example.s3test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
@Slf4j
public class TestController {

    private final S3ClientFactory s3ClientFactory;

    public TestController(S3ClientFactory factory) {
        this.s3ClientFactory = factory;
    }

    AtomicInteger counter = new AtomicInteger(0);

    @GetMapping("/doSomething")
    public ResponseEntity doStuff() throws InterruptedException {
        int requestNumber = counter.getAndIncrement();
        log.info("Request %d: %s".formatted(requestNumber, Thread.currentThread()
                                                                 .toString()));
        ResponseInputStream<GetObjectResponse> test;
        S3Client client = s3ClientFactory.createS3Connection();
        try {
            client.headBucket(builder -> builder.bucket("test"));
            test = client.getObject(builder -> builder.bucket("test-neu")
                                                      .key("sample.pdf"));

            log.info("Request %d: %s".formatted(requestNumber, Thread.currentThread()
                                                                     .toString()));

            ResponseEntity.BodyBuilder bodyBuilder = ResponseEntity.status(HttpStatus.OK);
            InputStreamResource inputStreamResource = new InputStreamResource(test);
            return bodyBuilder.body(inputStreamResource);
        } finally {
         //   client.close();
        }
    }
}

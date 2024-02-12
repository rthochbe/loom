package com.example.loom;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
@Slf4j
public class TestController {

    AtomicInteger counter = new AtomicInteger(0);

    @GetMapping("/doSomething")
    public ResponseEntity<Object> doStuff() throws InterruptedException {
        int requestNumber = counter.getAndIncrement();
        log.info("Request %d: %s".formatted(requestNumber, Thread.currentThread()
                                                                 .toString()));
        Thread.sleep(1000);
        log.info("Request %d: %s".formatted(requestNumber, Thread.currentThread()
                                                                 .toString()));
        var body = new DefaultResponse("do something");
        return ResponseEntity.ok(body);
    }
}

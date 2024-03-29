package com.example.loom;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
@Slf4j
public class TestController {

    private final RestTemplate resttemplate;

    public TestController(RestTemplate posResttemplate) {
        this.resttemplate=posResttemplate;
    }

    AtomicInteger counter = new AtomicInteger(0);

    @GetMapping("/doSomething")
    public ResponseEntity<Object> doStuff() throws InterruptedException {
        int requestNumber = counter.getAndIncrement();
        log.info("Request %d: %s".formatted(requestNumber, Thread.currentThread()
                                                                 .toString()));
        String response = resttemplate.getForObject("http://localhost:8070/doSomething", String.class);
        log.info("Request %d: %s".formatted(requestNumber, Thread.currentThread()
                                                                 .toString()));
        return ResponseEntity.ok(response);
    }
}

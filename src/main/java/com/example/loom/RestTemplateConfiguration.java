package com.example.loom;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {

    @Bean("posRestTemplate")
    public RestTemplate posRestTemplate(RestTemplateBuilder builder) {

        return builder.build();
    }
}


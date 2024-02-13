package com.example.s3test;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.core.retry.conditions.AndRetryCondition;
import software.amazon.awssdk.core.retry.conditions.RetryCondition;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Component
public class S3ClientFactory {

    private S3Client s3Client;

    public S3ClientFactory() {
            }

    public S3Client createS3Connection() {
        return createConnection();
    }

    private S3Client createConnection() {

        if (this.s3Client==null) {
            this.s3Client = S3Client.builder()
                                    .httpClientBuilder(ApacheHttpClient.builder()
                                                                       .maxConnections(50)
                                                                       .connectionTimeout(Duration.of(100, ChronoUnit.MILLIS))
                                                                       .connectionTimeToLive(Duration.of(1, ChronoUnit.MINUTES)))
                                    .credentialsProvider(
                                            StaticCredentialsProvider.create(AwsBasicCredentials.create(
                                                    "admin", "password")))
                                    .endpointOverride(URI.create("http://localhost:9000"))
                                    //this is default and should be used
                                    .region(Region.US_EAST_1)
                                    .serviceConfiguration(S3Configuration.builder()
                                                                         .pathStyleAccessEnabled(true)
                                                                         .build())
                                    .overrideConfiguration(ClientOverrideConfiguration.builder()
                                                                                      .apiCallTimeout(Duration.of(1,
                                                                                                                  ChronoUnit.SECONDS))
                                                                                      .addMetricPublisher(new LoggingMetricPublisher())
                                                                                      .retryPolicy(RetryPolicy.builder()
                                                                                                              .numRetries(3)
                                                                                                              .retryCondition(
                                                                                                                      AndRetryCondition.create(
                                                                                                                              RetryCondition.defaultRetryCondition()))
                                                                                                              .build())
                                                                                      .build())
                                    .build();
        }
        return this.s3Client;
    }
}

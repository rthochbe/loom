package com.example.s3test;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.metrics.MetricCollection;
import software.amazon.awssdk.metrics.MetricPublisher;

import java.time.Duration;

@Slf4j
public class LoggingMetricPublisher implements MetricPublisher {

    @Override
    public void publish(MetricCollection metricCollection) {
        log.debug("AWS_METRICS:{}", metricCollection);
        Duration apiCallDuration = Duration.ZERO;
        String operation = "";
        for (var metric : metricCollection) {
            var metricType = metric.metric();
            String name = metricType.name();
            if (name.equals("OperationName")) {
                operation = metric.value().toString();
            }
            if (name.equals("ApiCallDuration")) {
                Object value = metric.value();
                if (value.getClass() == Duration.class) {
                    apiCallDuration = (Duration) value;
                }
            }
        }
        log.info("S3 call for '{}' took {} ms", operation, apiCallDuration.getNano() / 1000000);
    }

    @Override
    public void close() {
        //since we are not opening any connections to write the metrics we don't need to do anything here
    }
}

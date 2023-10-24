package com.example.isolating_test_sqs_with_imposter.controller;

import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsListener {
    private final SqsTemplate sqsTemplate;


    @SqsListener("${sqs.out.queue}")
    public void callback(String message) {
        log.info("message receive {}", message);
    }

}

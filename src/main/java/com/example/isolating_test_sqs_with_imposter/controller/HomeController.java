package com.example.isolating_test_sqs_with_imposter.controller;


import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HomeController {
    private final SqsTemplate sqsTemplate;

    @Value("${sqs.in.queue}")
    private String queueInQueue;

    @PostMapping
    public String inputFlow() {
        return sqsTemplate.send(queueInQueue, "random message")
                .messageId()
                .toString();
    }
}

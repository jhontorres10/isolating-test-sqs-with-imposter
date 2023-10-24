package com.example.isolating_test_sqs_with_imposter;

import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@Service
public class InterceptorListener {
    private final SqsTemplate sqsTemplate;

    public InterceptorListener(SqsTemplate sqsTemplate) {
        this.sqsTemplate = sqsTemplate;
    }
    @Value("${sqs.out.queue}")
    private String queueOutQueue;

    @Value("${sqs.in.queue}")
    private String queueInQueue;


    @SqsListener("${sqs.in.queue}")
    public void callback(String message) throws URISyntaxException, IOException, InterruptedException {
        var request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8090/" + queueInQueue))
                .GET()
                .build();
        var client = HttpClient.newBuilder().build();
        var responseMock = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        sqsTemplate.send(queueOutQueue, responseMock);
    }

}

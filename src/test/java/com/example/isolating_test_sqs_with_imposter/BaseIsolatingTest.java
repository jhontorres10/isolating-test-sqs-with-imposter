package com.example.isolating_test_sqs_with_imposter;


import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.List;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

@Testcontainers
@SpringBootTest(classes = {BaseIsolatingTest.class})
public abstract class BaseIsolatingTest {

    private static final List<String> queueNames = List.of("in-queue", "out-queue");
    private static final DockerImageName LOCALSTACK_IMAGE = DockerImageName.parse("localstack/localstack");

    @Container
    public static final LocalStackContainer LOCALSTACK_CONTAINER = new LocalStackContainer(LOCALSTACK_IMAGE)
            .withServices(SQS);

    @BeforeAll
    static void setUp() {
        queueNames.forEach(BaseIsolatingTest::createQueue);
    }

    private static void createQueue(String queueName) {
        try {
            LOCALSTACK_CONTAINER.execInContainer(
                    "awslocal",
                    "sqs",
                    "create-queue",
                    "--queue-name",
                    queueName
            );
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

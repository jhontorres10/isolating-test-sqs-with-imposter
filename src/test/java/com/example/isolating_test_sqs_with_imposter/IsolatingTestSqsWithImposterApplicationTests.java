package com.example.isolating_test_sqs_with_imposter;

import com.example.isolating_test_sqs_with_imposter.controller.AwsListener;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;


@WireMockTest(httpPort = 8090) // imposter
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class IsolatingTestSqsWithImposterApplicationTests extends BaseIsolatingTest {

	@Autowired
	private MockMvc mockMvc;

	@SpyBean
	private AwsListener awsListener;

	@Value("${sqs.in.queue}")
	private String queueInQueue;



	@Test
	void contextLoads() throws Exception {
		String message = "messageStubbed";

		stubFor(get("/" + queueInQueue)
				.willReturn(aResponse()
						.withHeader("Content-Type", "application/json")
						.withBody(message)));

		mockMvc.perform(MockMvcRequestBuilders.post("/"))
				.andDo(print())
				.andExpect(status().isOk());

		await().atMost(Duration.ofSeconds(3))
				.untilAsserted( () -> {
					verify(awsListener).callback(message);
				});
	}

}

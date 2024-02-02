package edu.michalvavrik.deadletterqueue;

import io.quarkus.test.junit.QuarkusTest;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class DeadLetterQueueTest {

    static final int ASSERT_TIMEOUT_SECONDS = 30;
    static final List<String> EXPECTED_MESSAGES = Arrays.asList("10", "30", "40", "50", "60", "70", "80", "90", "100");
    static final List<String> DEAD_LETTER_MESSAGE = Arrays.asList("20");

    @Test
    public void testNormalMessage() {
        await().pollInterval(1, TimeUnit.SECONDS)
                .atMost(ASSERT_TIMEOUT_SECONDS, TimeUnit.SECONDS).untilAsserted(() -> {
                    String response = given().get("/message")
                            .then().statusCode(HttpStatus.SC_OK).extract().asString();
                    assertTrue(EXPECTED_MESSAGES.stream().anyMatch(response::contains),
                            "Expected message not found in " + response);
                });
    }

    @Test
    public void testDeadLetterMessage() {
        await().pollInterval(1, TimeUnit.SECONDS)
                .atMost(ASSERT_TIMEOUT_SECONDS, TimeUnit.SECONDS).untilAsserted(() -> {
                    String response = given().get("/dead-letter-msg")
                            .then().statusCode(HttpStatus.SC_OK).extract().asString();
                    assertTrue(DEAD_LETTER_MESSAGE.stream().anyMatch(response::contains),
                            "Expected message not found in " + response);
                });
    }

    @Test
    public void testRetriedMessage() {
        await().pollInterval(1, TimeUnit.SECONDS)
                .atMost(ASSERT_TIMEOUT_SECONDS, TimeUnit.SECONDS).untilAsserted(() -> {
                    String response = given().get("/message")
                            .then().statusCode(HttpStatus.SC_OK).extract().asString();
                    assertTrue(response.contains("21"));
                });
    }
}

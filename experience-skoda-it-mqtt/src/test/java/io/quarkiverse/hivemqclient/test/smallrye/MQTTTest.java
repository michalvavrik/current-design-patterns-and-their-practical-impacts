package io.quarkiverse.hivemqclient.test.smallrye;

import static io.quarkiverse.hivemqclient.test.smallrye.GreetingGenerator.EXPECTED_GREETINGS;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.SseEventSource;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.http.TestHTTPResource;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class MQTTTest {
    private static final int TIMEOUT_SEC = 5;
    private static final Logger LOG = Logger.getLogger(MQTTTest.class);

    @TestHTTPResource("greetings/stream")
    URI greetingsUrl;

    @Test
    public void getGreeting() {
        given()
                .when().get("/greetings")
                .then()
                .statusCode(200)
                .body(is("hello"));
    }

    @Test
    public void streamGreetings() {
        AtomicInteger totalAmountReceived = new AtomicInteger(0);
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(greetingsUrl);
        CountDownLatch expectedAmount = new CountDownLatch(5);
        Set<String> actualGreetings = ConcurrentHashMap.newKeySet();
        try (SseEventSource source = SseEventSource.target(target).build()) {
            source.register(event -> {
                String value = event.readData(String.class, MediaType.APPLICATION_JSON_TYPE);
                LOG.infof("Received greeting: %s", value);
                totalAmountReceived.incrementAndGet();
                actualGreetings.add(value);
            });
            source.open();
            expectedAmount.await(TIMEOUT_SEC, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
        } finally {
            int received = totalAmountReceived.get();
            Assertions.assertTrue(received > 0);
            // greetings are generated before test starts consuming them, so it will always be just small subset of them
            List<String> expectedGreetings = Arrays.stream(EXPECTED_GREETINGS).map(greeting -> greeting + "!").toList();
            Assertions.assertTrue(expectedGreetings.containsAll(actualGreetings));
        }
    }

}

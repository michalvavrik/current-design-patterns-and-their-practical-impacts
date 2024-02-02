package edu.michalvavrik.deadletterqueue;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.sse.SseEventSource;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@QuarkusTest
public class DeadLetterQueueTest {

    static final int ASSERT_TIMEOUT_SECONDS = 30;

    @TestHTTPResource("/consumed-movies")
    URI consumedMovies;

    // THIS COMES FROM QUARKUS QUICKSTART AND IT IS NOT PART OF THE EXAMPLE
    @Order(1)
    @Test
    public void testRegularMessagingWorks() throws InterruptedException {
        // create a client for `ConsumedMovieResource` and collect the consumed resources in a list
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(consumedMovies);

        List<String> received = new CopyOnWriteArrayList<>();

        SseEventSource source = SseEventSource.target(target).build();
        source.register(inboundSseEvent -> received.add(inboundSseEvent.readData()));

        // in a separate thread, feed the `MovieResource`
        ExecutorService movieSender = startSendingMovies();

        source.open();

        // check if, after at most 5 seconds, we have at least 2 items collected, and they are what we expect
        await().atMost(5, SECONDS).until(() -> received.size() >= 2);
        assertThat(received, Matchers.hasItems("'The Shawshank Redemption' from 1994",
                "'12 Angry Men' from 1957"));
        source.close();

        // shutdown the executor that is feeding the `MovieResource`
        movieSender.shutdownNow();
        movieSender.awaitTermination(5, SECONDS);
    }

    @Order(2)
    @Test
    public void testDeadLetterQueue() {
        // test failed message retry
        await().pollInterval(1, TimeUnit.SECONDS)
                .atMost(ASSERT_TIMEOUT_SECONDS, TimeUnit.SECONDS).untilAsserted(() -> {
                    String response = given().get("/message")
                            .then().statusCode(HttpStatus.SC_OK).extract().asString();
                    assertTrue(response.contains("334"));
                });
    }

    private ExecutorService startSendingMovies() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            while (true) {
                given()
                        .contentType(ContentType.JSON)
                        .body("{\"title\":\"The Shawshank Redemption\",\"year\":1994}")
                        .when()
                        .post("/movies")
                        .then()
                        .statusCode(202);

                given()
                        .contentType(ContentType.JSON)
                        .body("{\"title\":\"12 Angry Men\",\"year\":1957}")
                        .when()
                        .post("/movies")
                        .then()
                        .statusCode(202);

                try {
                    Thread.sleep(200L);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        return executorService;
    }
}

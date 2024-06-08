package edu.michalvavrik.resilience;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.is;

@QuarkusTest
public class CircuitBreakerTest {

    @Test
    public void testAnnotation() throws InterruptedException {
        RestAssured.get("/circuit-breaker/annotation")
                .then()
                .statusCode(500);
        RestAssured.get("/circuit-breaker/annotation")
                .then()
                .statusCode(200)
                .body(is("annotation"));
        RestAssured.get("/circuit-breaker/annotation")
                .then()
                .statusCode(500);
        RestAssured.get("/circuit-breaker/annotation")
                .then()
                .statusCode(200)
                .body(is("annotation"));

        // threshold reached, expect just failures as circuit is opened
        RestAssured.get("/circuit-breaker/annotation")
                .then()
                .statusCode(500);
        RestAssured.get("/circuit-breaker/annotation")
                .then()
                .statusCode(500);
        RestAssured.get("/circuit-breaker/annotation")
                .then()
                .statusCode(500);

        // wait for 6 seconds and expect success
        Thread.sleep(Duration.ofSeconds(6));
        RestAssured.get("/circuit-breaker/annotation")
                .then()
                .statusCode(200)
                .body(is("annotation"));
    }

}

package edu.michalvavrik.resilience;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;

import static org.hamcrest.Matchers.is;

@QuarkusTest
public class ThrottlingTest {

    @Test
    public void testRateLimitingWithCircuitBreaker() {
        var response = new ArrayList<String>();
        for (int i = 0; i < 100; i++) {
            response.add(RestAssured.get("/circuit-breaker/throttling")
                    .then()
                    .statusCode(200)
                    .extract()
                    .asString());
        }
        Assertions.assertEquals(100, response.size());
        Assertions.assertTrue(response.contains("hello"));
        Assertions.assertTrue(response.contains("circuit-breaker-fallback"));
    }

}

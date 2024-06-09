package edu.michalvavrik.resilience;

import com.google.common.util.concurrent.RateLimiter;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertEquals(100, response.size());
        assertTrue(response.contains("hello"));
        assertTrue(response.contains("circuit-breaker-fallback"));
    }

    @Test
    public void testSimplisticThrottling() {
        Supplier<Integer> intSupplier = () -> 1;
        var rateLimiter = new RateLimiter(5);
        for (int i = 0; i < 5; i++) {
            assertEquals(1, rateLimiter.apply(intSupplier));
        }
        assertThrows(RuntimeException.class, () -> rateLimiter.apply(intSupplier));
    }

    public static final class RateLimiter {

        private volatile Instant start = Instant.now();
        private final AtomicInteger callsCount = new AtomicInteger(0);
        private final int maxCallsPerSecond;

        public RateLimiter(int maxCallsPerSecond) {
            this.maxCallsPerSecond = maxCallsPerSecond;
        }

        public <T> T apply(Supplier<T> rateLimitedAction) {
            if (numOfCallsPerSec() <= maxCallsPerSecond) {
                return rateLimitedAction.get();
            } else {
                throw new RuntimeException("Rate limit exceeded");
            }
        }

        private int numOfCallsPerSec() {
            if (start.plusSeconds(1).isBefore(Instant.now())) {
                start = Instant.now();
                callsCount.set(0);
            }
            return callsCount.incrementAndGet();
        }

    }
}

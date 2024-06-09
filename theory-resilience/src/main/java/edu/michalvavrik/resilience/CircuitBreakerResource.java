package edu.michalvavrik.resilience;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.concurrent.atomic.AtomicLong;

import static java.time.temporal.ChronoUnit.SECONDS;

@Path("circuit-breaker")
public class CircuitBreakerResource {

    private final AtomicLong annotationCounter = new AtomicLong(0);

    @RestClient
    RateLimitingClient rateLimitingClient;

    @GET
    @CircuitBreaker(requestVolumeThreshold = 4, delay = 5, delayUnit = SECONDS)
    @Path("annotation")
    public String annotation() {
        var count = annotationCounter.incrementAndGet();
        if (count % 2 == 0 || count > 4) {
            return "annotation";
        }
        throw new InternalServerErrorException();
    }

    @Fallback(fallbackMethod = "throttlingFallback")
    @GET
    @CircuitBreaker(requestVolumeThreshold = 4, delay = 5, delayUnit = SECONDS)
    @Path("throttling")
    public String throttling() {
        return rateLimitingClient.hello();
    }

    public String throttlingFallback() {
        return "circuit-breaker-fallback";
    }
}
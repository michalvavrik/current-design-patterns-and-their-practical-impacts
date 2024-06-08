package edu.michalvavrik.resilience;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;

import java.util.concurrent.atomic.AtomicLong;

import static java.time.temporal.ChronoUnit.SECONDS;

@Path("circuit-breaker")
public class CircuitBreakerResource {

    private AtomicLong annotationCounter = new AtomicLong(0);

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

}
